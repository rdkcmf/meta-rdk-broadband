SUMMARY = "RDK-WiFi-LIBHOSTAP for RDK CcspWiFiAgent components"
SUMMARY = "This recipe compiles and installs the Opensource hostapd as a dynamic library for RDK hostap authenticator"
SECTION = "base"
LICENSE = "CLOSED"

FILESEXTRAPATHS_prepend:="${THISDIR}/files:"
PROVIDES = "rdk-wifi-libhostap"
RPROVIDES_${PN} = "rdk-wifi-libhostap"
DEPENDS = "libnl openssl"
CFLAGS_prepend += "-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/libnl3 "
CFLAGS_prepend += "-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/ "

inherit autotools pkgconfig

SRC_URI = "${RDKB_CCSP_ROOT_GIT}/rdk-wifi-libhostap;protocol=${RDK_GIT_PROTOCOL};branch=${RDK_GIT_BRANCH};name=rdk-wifi-libhostap"
SRCREV_rdk-wifi-libhostap = "${AUTOREV}"
SRCREV_FORMAT = "rdk-wifi-libhostap"

CFLAGS_append = " \
    -DCONFIG_LIBNL32 \
    -DCONFIG_LIBNL20 \
    -DCONFIG_DRIVER_NL80211 \
    -DCONFIG_HS20 \
    -DCONFIG_INTERWORKING \
    -DCONFIG_ACS \
    -DFEATURE_SUPPORT_RADIUSGREYLIST \
"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'OneWifi', '-DRDK_ONEWIFI', '', d)}"

#Lib hostap compilation changes for compiling libhostap.so
#!!This has to be first patch!!
SRC_URI += " ${@bb.utils.contains('DISTRO_FEATURES', 'HOSTAPD_2_10', 'file://2.10/oneWifiLib.patch file://2.10/greylist.patch file://2.10/broadcom.patch file://2.10/one_wifi_radius_greylist.patch',\ 
              'file://2.9/hostapd-lib-build-modify.patch file://2.9/hostapd-logger-module-changes.patch file://2.9/lib-hostap-changes-xb7.diff \
              file://2.9/wps.patch file://2.9/eloop_rfc_switch.patch file://2.9/greylist.patch file://2.9/one_wifi.patch file://2.9/one_wifi_bss_transition.patch file://2.9/one_wifi_radius_greylist.patch', d)}"


S = "${WORKDIR}/git/"

do_install_append () {
        install -d ${D}${includedir}/rdk-wifi-libhostap/
        install -d ${D}${includedir}/rdk-wifi-libhostap/src/
        install -d ${D}${includedir}/rdk-wifi-libhostap/src/hostapd/
        if ${@bb.utils.contains('DISTRO_FEATURES', 'HOSTAPD_2_10', 'true', 'false', d)}; then
            cd ${S}/source/hostap-2.10/src && find . -type f -name "*.h" -exec install -D -m 0755 "{}" ${D}${includedir}/rdk-wifi-libhostap/src/"{}" \;
            install -m 0755 ${S}/source/hostap-2.10/hostapd/ctrl_iface.h ${D}${includedir}/rdk-wifi-libhostap/src/hostapd/
            if ${@bb.utils.contains('DISTRO_FEATURES', 'OneWifi', 'true', 'false', d)}; then
                install -m 0755 ${S}/source/hostap-2.10/hostapd/eap_register.h ${D}${includedir}/rdk-wifi-libhostap/src/hostapd/
            fi
            install -m 0755 ${S}/source/hostap-2.10/hostapd/config_file.h ${D}${includedir}/rdk-wifi-libhostap/src/hostapd/
        else
            cd ${S}/source/hostap-2.9/src && find . -type f -name "*.h" -exec install -D -m 0755 "{}" ${D}${includedir}/rdk-wifi-libhostap/src/"{}" \;
            install -m 0755 ${S}/source/hostap-2.9/hostapd/ctrl_iface.h ${D}${includedir}/rdk-wifi-libhostap/src/hostapd/
            if ${@bb.utils.contains('DISTRO_FEATURES', 'OneWifi', 'true', 'false', d)}; then
                install -m 0755 ${S}/source/hostap-2.9/hostapd/eap_register.h ${D}${includedir}/rdk-wifi-libhostap/src/hostapd/
            fi
            install -m 0755 ${S}/source/hostap-2.9/hostapd/config_file.h ${D}${includedir}/rdk-wifi-libhostap/src/hostapd/
        fi

}

FILES_${PN} = " \
        ${libdir}/libhostap.so* \
        ${PKG_CONFIG_SYSROOT_DIR}${includedir}/libnl/* \
"
