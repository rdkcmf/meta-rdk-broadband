SUMMARY = "CCSP CcspLMLite component"
HOMEPAGE = "http://github.com/belvedere-yocto/CcspLMLite"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

DEPENDS = "ccsp-common-library utopia avro-c msgpack-c trower-base64 util-linux curl libxml2 wrp-c nanomsg libparodus telemetry libsyswrapper libunpriv hal-platform"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
DEPENDS_append = "${@bb.utils.contains("DISTRO_FEATURES", "WanFailOverSupportEnable", " rbus ", " ", d)}"
require ccsp_common.inc

SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/CcspLMLite;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=CcspLMLite"

SRCREV_CcspLMLite = "${AUTOREV}"
SRCREV_FORMAT = "CcspLMLite"

PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig pythonnative breakpad-logmapper

CFLAGS += " \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_INCDIR}/libxml2 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I${STAGING_INCDIR}/mlt \
    -I${STAGING_INCDIR}/trower-base64 \
    -I${STAGING_INCDIR}/libparodus \
    "

CFLAGS += " -Wall -Werror -Wextra "


LDFLAGS_append = " \
    -lcurl \
    -lxml2 \
    -ldbus-1 \
    -lprivilege \
    -lhal_platform \
    "

EXTRA_OECONF_append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", " --enable-seshat ", " ", d)}"
DEPENDS_append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", " libseshat ", " ", d)}"
CFLAGS_append = "${@bb.utils.contains("DISTRO_FEATURES", "seshat", " -DENABLE_SESHAT ", " ", d)}"
CFLAGS_append = "\
    ${@bb.utils.contains("DISTRO_FEATURES", "seshat", "-I${STAGING_INCDIR}/libseshat ", " ", d)} \
"
CFLAGS_append = "${@bb.utils.contains("DISTRO_FEATURES", "mlt", " -DMLT_ENABLED -DUSE_SYSRES_MLT=1 -DRETURN_ADDRESS_LEVEL=0 ", " ", d)}"
LDFLAGS_append = "${@bb.utils.contains("DISTRO_FEATURES", "mlt", " -lsysResource ", " ", d)}"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"

LDFLAGS_append = " ${@bb.utils.contains("DISTRO_FEATURES", 'wan-traffic-count', ' -lrbus -lrbuscore -lrtMessage ', '', d)}"
CFLAGS_append  = " ${@bb.utils.contains('DISTRO_FEATURES', 'wan-traffic-count', ' -I${STAGING_INCDIR}/rbus -I${STAGING_INCDIR}/rtmessage ', '', d)}"
CFLAGS_append  = " ${@bb.utils.contains('DISTRO_FEATURES', 'wan-traffic-count', ' -DWAN_TRAFFIC_COUNT_SUPPORT', '', d)}"

LDFLAGS_append = "${@bb.utils.contains("DISTRO_FEATURES", "WanFailOverSupportEnable", " -lrbus -lrbuscore -lrtMessage ", " ", d)}"
CFLAGS_append = "${@bb.utils.contains("DISTRO_FEATURES", "WanFailOverSupportEnable", " -I${STAGING_INCDIR}/rbus -I${STAGING_INCDIR}/rtmessage ", " ", d)}"
CFLAGS_append = " ${@bb.utils.contains("DISTRO_FEATURES", "WanFailOverSupportEnable", " -DWAN_FAILOVER_SUPPORTED ", " ", d)} "

do_compile_prepend () {
	(python ${STAGING_BINDIR_NATIVE}/dm_pack_code_gen.py ${S}/config/LMLite.XML ${S}/source/Ssp/dm_pack_datamodel.c)
}

#force lib to be built first
do_compile () {
    oe_runmake liblmapi.la
    oe_runmake all
}

do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/ccsp/lm
    ln -sf /usr/bin/CcspLMLite ${D}${prefix}/ccsp/lm/CcspLMLite
    install -m 644 ${S}/config/NetworkDevicesStatus.avsc ${D}${prefix}/ccsp/lm/NetworkDevicesStatus.avsc
    install -m 644 ${S}/config/NetworkDevicesTraffic.avsc ${D}${prefix}/ccsp/lm/NetworkDevicesTraffic.avsc
    install -d ${D}/${includedir}
    install -m 644 ${S}/source/lm/lm_api.h -t ${D}/${includedir}
}

PACKAGES += "${PN}-ccsp"

FILES_${PN}-ccsp = " \
    ${prefix}/ccsp/lm/CcspLMLite \
    ${prefix}/ccsp/lm/NetworkDevicesStatus.avsc  \
    ${prefix}/ccsp/lm/NetworkDevicesTraffic.avsc  \
"

FILES_${PN}-dbg = " \
    ${prefix}/ccsp/lm/.debug \
    ${prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

PACKAGES =+ "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

FILES_${PN}-gtest = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/CcspLMLite_gtest.bin', '', d)} \
"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-CcspLMLite', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="gtest"
SKIP_MAIN_PKG="yes"
DOWNLOAD_ON_DEMAND="yes"
# Breakpad processname and logfile mapping
BREAKPAD_LOGMAPPER_PROCLIST = "CcspLMLite"
BREAKPAD_LOGMAPPER_LOGLIST = "LM.txt.0"
