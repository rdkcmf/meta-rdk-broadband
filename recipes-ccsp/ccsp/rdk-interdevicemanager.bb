SUMMARY = "RDK Inter-Device Manager component"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"


DEPENDS = "ccsp-common-library rdk-logger utopia libunpriv"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

SRC_URI ="${RDKB_CCSP_ROOT_GIT}/RdkInterDeviceManager/generic;protocol=${RDK_GIT_PROTOCOL};branch=${CCSP_GIT_BRANCH};name=InterDeviceManager"

SRCREV_InterDeviceManager = "${AUTOREV}"
SRCREV_FORMAT = "InterDeviceManager"

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
LDFLAGS_append_dunfell = " -ldbus-1"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'safec dunfell', ' -lsafec-3.5.1 ', '', d)}"
CFLAGS_prepend += " ${@bb.utils.contains('DISTRO_FEATURES', 'IDM_DEBUG',' -DIDM_DEBUG','', d)}"
PACKAGES += "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

SYSTEMD_SERVICE_${PN} = "RdkInterDeviceManager.service"

do_install_append () {
    # Config files and scripts
    install -d ${D}${exec_prefix}/rdk/interdevicemanager
    ln -sf ${bindir}/interdevicemanager ${D}${exec_prefix}/rdk/interdevicemanager/interdevicemanager
    if ${@bb.utils.contains('DISTRO_FEATURES', 'IDM_DEBUG','true','false', d)}; then
    sed -i '/idm_certs.sh/d' ${S}/systemd_units/RdkInterDeviceManager.service
    fi
    #Install systemd unit.
    install -d ${D}${systemd_unitdir}/system
    install -D -m 0644 ${S}/systemd_units/RdkInterDeviceManager.service ${D}${systemd_unitdir}/system/RdkInterDeviceManager.service
}


FILES_${PN} = " \
   ${bindir}/* \
   ${exec_prefix}/rdk/interdevicemanager/* \
   ${systemd_unitdir}/system/RdkInterDeviceManager.service \
"

FILES_${PN}-dbg = " \
    ${exec_prefix}/rdk/rdkinterdevicemanager/.debug \
    /usr/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

FILES_${PN}-gtest = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/RdkInterDeviceManager_gtest.bin', '', d)} \
"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-RdkInterDeviceManager', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtest', '', d)}"
SKIP_MAIN_PKG="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
DOWNLOAD_ON_DEMAND="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
