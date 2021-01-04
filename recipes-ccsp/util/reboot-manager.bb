SUMMARY = "CCSP Reboot Manager utility."
HOMEPAGE = "http://github.com/belvedere-yocto/RebootManager"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library"
require recipes-ccsp/ccsp/ccsp_common.inc
SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/RebootManager;protocol=${CMF_GIT_PROTOCOL};branch=master;name=RebootManager"

SRCREV_RebootManager = "${AUTOREV}"
SRCREV_FORMAT = "RebootManager"
PV = "${RDK_RELEASE}+git${SRCPV}"

DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

S = "${WORKDIR}/git"

inherit autotools

CFLAGS += " -Wall -Werror -Wextra -Wno-unused-parameter"

CFLAGS_append = " \
    -I=${includedir}/dbus-1.0 \
    -I=${libdir}/dbus-1.0/include \
    -I=${includedir}/ccsp \
    "

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"

LDFLAGS_append = " \
    -ldbus-1 \
    "

do_configure_append_qemux86 () {
    install -m 644 ${S}/source-pc/CcspRmHal.c -t ${S}/source/RmSsp
}

do_configure_append_qemuarm () {
    install -m 644 ${S}/source-arm/CcspRmHal.c -t ${S}/source/RmSsp
}

do_configure_append_raspberrypi () {
    install -m 644 ${S}/source-arm/CcspRmHal.c -t ${S}/source/RmSsp
}

do_configure_append_armeb () {
    install -m 644 ${S}/source-arm/CcspRmHal.c -t ${S}/source/RmSsp
}

do_configure_append_arrisxb6atom () {
    install -m 644 ${S}/source-arm/CcspRmHal.c -t ${S}/source/RmSsp
}

do_configure_append_mips () {
    install -m 644 ${S}/source-mips/CcspRmHal.c -t ${S}/source/RmSsp
}

do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/ccsp/rm
    ln -sf /usr/bin/CcspRmSsp ${D}/usr/ccsp/rm/CcspRmSsp
}


do_install_append_qemux86 () {
    # Config files and scripts
    install -m 644 ${S}/config/RebootManager_pc.xml ${D}/usr/ccsp/rm/RebootManager.xml 
}

do_install_append_qemuarm () {
    # Config files and scripts
    install -m 644 ${S}/config/RebootManager_arm.xml ${D}/usr/ccsp/rm/RebootManager.xml 
}

do_install_append_raspberrypi () {
    # Config files and scripts
    install -m 644 ${S}/config/RebootManager_arm.xml ${D}/usr/ccsp/rm/RebootManager.xml 
}

do_install_append_armeb () {
    # Config files and scripts
    install -m 644 ${S}/config/RebootManager_arm.xml ${D}/usr/ccsp/rm/RebootManager.xml 
}

do_install_append_mips () {
    # Config files and scripts
    install -m 644 ${S}/config/RebootManager_mips.xml ${D}/usr/ccsp/rm/RebootManager.xml 
}

do_install_append_mipsel () {
    # Config files and scripts
    install -m 644 ${S}/config/RebootManager_mips.xml ${D}/usr/ccsp/rm/RebootManager.xml 
}

PACKAGES += "${PN}-ccsp"

FILES_${PN}-ccsp = " \
    ${prefix}/ccsp/rm/CcspRmSsp \
    ${prefix}/ccsp/rm/RebootManager.xml \
"

FILES_${PN}-dbg = " \
    ${prefix}/ccsp/rm/.debug \
    ${prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"
