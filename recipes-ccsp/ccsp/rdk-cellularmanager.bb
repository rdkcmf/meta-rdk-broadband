SUMMARY = "RDK Cellular Manager component"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library rdk-logger utopia libunpriv halinterface glib-2.0 libqmi libgudev"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

SRC_URI ="${RDKB_CCSP_ROOT_GIT}/RdkCellularManager/generic;protocol=${RDK_GIT_PROTOCOL};branch=${CCSP_GIT_BRANCH};name=CellularManager"

SRCREV_CellularManager = "${AUTOREV}"
SRCREV_FORMAT = "CellularManager"

PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

require ccsp_common.inc

inherit autotools pkgconfig systemd

CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I${STAGING_INCDIR}/libsafec \
    -I${STAGING_INCDIR}/glib-2.0 \
    -I${STAGING_LIBDIR}/glib-2.0/include \
    -I${STAGING_INCDIR}/libqmi-glib \
    "
LDFLAGS += " -lprivilege"
LDFLAGS_append_dunfell = " -ldbus-1"
LDFLAGS += " -lgobject-2.0 -lgio-2.0 -lglib-2.0 -lgudev-1.0 -lqmi-glib"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'safec dunfell', ' -lsafec-3.5.1 ', '', d)}"

PACKAGES += "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

SYSTEMD_SERVICE_${PN} = "RdkCellularManager.service"

do_install_append () {
    # Config files and scripts
    install -d ${D}${exec_prefix}/rdk/cellularmanager
    ln -sf ${bindir}/cellularmanager ${D}${exec_prefix}/rdk/cellularmanager/cellularmanager
    install -m 644 ${S}/config/RdkCellularManager.xml ${D}${exec_prefix}/rdk/cellularmanager/
    
    #Install systemd unit.
    install -d ${D}${systemd_unitdir}/system
    install -D -m 0644 ${S}/systemd_units/RdkCellularManager.service ${D}${systemd_unitdir}/system/RdkCellularManager.service
}

FILES_${PN} = " \
   ${bindir}/* \
   ${exec_prefix}/rdk/cellularmanager/* \
   ${systemd_unitdir}/system/RdkCellularManager.service \
"

FILES_${PN}-dbg = " \
    ${exec_prefix}/rdk/rdkcellularmanager/.debug \
    /usr/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

FILES_${PN}-gtest = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/RdkCellularManager_gtest.bin', '', d)} \
"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-RdkCellularManager', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtest', '', d)}"
SKIP_MAIN_PKG="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
DOWNLOAD_ON_DEMAND="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
