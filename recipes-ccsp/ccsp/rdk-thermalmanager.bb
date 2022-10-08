SUMMARY = "RDK Thermal Manager component"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"


DEPENDS = "ccsp-common-library rdk-logger utopia libunpriv"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

SRC_URI ="${RDKB_CCSP_ROOT_GIT}/RdkThermalManager/generic;protocol=${RDK_GIT_PROTOCOL};branch=${CCSP_GIT_BRANCH};name=ThermalManager"

SRCREV_ThermalManager = "${AUTOREV}"
SRCREV_FORMAT = "ThermalManager"

PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

require ccsp_common.inc

inherit autotools pkgconfig systemd

CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    "

LDFLAGS += " -lprivilege"
LDFLAGS_append_dunfell = " -ldbus-1 -lsyscfg"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'safec dunfell', ' -lsafec-3.5.1 ', '', d)}"

PACKAGES += "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

SYSTEMD_SERVICE_${PN} = "RdkThermalManager.service"

do_install_append () {
    # Config files and scripts
    install -d ${D}${exec_prefix}/rdk/thermalmanager
    ln -sf ${bindir}/thermalmanager ${D}${exec_prefix}/rdk/thermalmanager/thermalmanager
    install -m 644 ${S}/config/RdkThermalManager.xml ${D}${exec_prefix}/rdk/thermalmanager/

    #Install systemd unit.
    install -d ${D}${systemd_unitdir}/system
    install -D -m 0644 ${S}/systemd_units/RdkThermalManager.service ${D}${systemd_unitdir}/system/RdkThermalManager.service
}


FILES_${PN} = " \
   ${bindir}/* \
   ${exec_prefix}/rdk/thermalmanager/* \
   ${systemd_unitdir}/system/RdkThermalManager.service \
"

FILES_${PN}-dbg = " \
    ${exec_prefix}/rdk/rdkthermalmanager/.debug \
    /usr/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

FILES_${PN}-gtest = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/RdkThermalManager_gtest.bin', '', d)} \
"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-RdkThermalManager', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtest', '', d)}"
SKIP_MAIN_PKG="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
DOWNLOAD_ON_DEMAND="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"

