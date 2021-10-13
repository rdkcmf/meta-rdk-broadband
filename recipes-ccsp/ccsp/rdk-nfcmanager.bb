SUMMARY = "RDK NFC Manager component"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library rdk-logger utopia libunpriv halinterface hal-nfc"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

SRC_URI ="${RDKB_CCSP_ROOT_GIT}/RdkNfcManager/generic;protocol=${RDK_GIT_PROTOCOL};branch=${CCSP_GIT_BRANCH};name=NfcManager"

SRCREV_NfcManager = "${AUTOREV}"
SRCREV_FORMAT = "NfcManager"

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

PACKAGES += "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

SYSTEMD_SERVICE_${PN} = "RdkNfcManager.service"

do_install_append () {
    # Config files and scripts
    install -d ${D}${exec_prefix}/rdk/nfcmanager
    ln -sf ${bindir}/nfcmanager ${D}${exec_prefix}/rdk/nfcmanager/nfcmanager
    install -m 644 ${S}/config/RdkNFCManager.xml ${D}${exec_prefix}/rdk/nfcmanager/

    #Install systemd unit.
    install -d ${D}${systemd_unitdir}/system
    install -D -m 0644 ${S}/systemd_units/RdkNfcManager.service ${D}${systemd_unitdir}/system/RdkNfcManager.service
    install -m 0755 ${S}/systemd_units/NfcManager_check.sh ${D}${exec_prefix}/rdk/nfcmanager
}


FILES_${PN} = " \
   ${bindir}/* \
   ${exec_prefix}/rdk/nfcmanager/* \
   ${systemd_unitdir}/system/RdkNfcManager.service \
"

FILES_${PN}-dbg = " \
    ${exec_prefix}/rdk/rdknfcmanager/.debug \
    /usr/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

FILES_${PN}-gtest = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/RdkNfcManager_gtest.bin', '', d)} \
"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-RdkNfcManager', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtest', '', d)}"
SKIP_MAIN_PKG="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
DOWNLOAD_ON_DEMAND="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
