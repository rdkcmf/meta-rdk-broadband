SUMMARY = "CCSP XDNS component"
HOMEPAGE = "http://github.com/belvedere-yocto/CcspXDNS"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "ccsp-common-library dbus rdk-logger utopia trower-base64"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

require recipes-ccsp/ccsp/ccsp_common.inc

RDEPENDS_${PN} = " trower-base64 "
DEPENDS += " trower-base64"

SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/CcspXDNS;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=CcspXDNS"

SRCREV_CcspXDNS = "${AUTOREV}"
SRCREV_FORMAT = "CcspXDNS"
PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig pythonnative

CFLAGS += " -Wall -Werror -Wextra "

CFLAGS_append_dunfell = " -Wno-format-truncation -Wno-format-overflow "

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
LDFLAGS_remove_dunfell = "${@bb.utils.contains('DISTRO_FEATURES', 'safec', '-lsafec-3.5', '', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'safec dunfell', ' -lsafec-3.5.1 ', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"

CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I${STAGING_INCDIR}/utapi \
    -I${STAGING_INCDIR}/utctx \
    -I${STAGING_INCDIR}/ulog \
    -I${STAGING_INCDIR}/trower-base64 \
    "

LDFLAGS_append = " \
    -ldbus-1 \
    -lutctx \
    -lutapi \
    "

do_compile_prepend () {
    (python ${STAGING_BINDIR_NATIVE}/dm_pack_code_gen.py ${S}/config/CcspXdns_dm.xml ${S}/source/XdnsSsp/dm_pack_datamodel.c)
}

do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/ccsp/xdns
}

PACKAGES += "${PN}-ccsp"

FILES_${PN} += " \
    ${prefix}/ccsp/xdns \
    ${libdir}/libdmlxdns.so.* \
    ${bindir}/* \
"

FILES_${PN}-dbg += " \
    ${prefix}/ccsp/xdns/.debug \
    ${prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"
           
