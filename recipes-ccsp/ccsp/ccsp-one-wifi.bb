DEPENDS_append = " libunpriv "
LDFLAGS_append = " \
                 -lprivilege \
                 "

SUMMARY = "CCSP OneWifi component"
HOMEPAGE = "http://github.com/belvedere-yocto/OneWifi"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=042d68aa6c083a648f58bb8d224a4d31"
DEPENDS = "hal-wifi webconfig-framework telemetry libsyswrapper libev rbus libnl ccsp-one-wifi-libwebconfig trower-base64"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
#DEPENDS_append = " hal-cm  hal-dhcpv4c hal-ethsw hal-moca hal-mso_mgmt hal-mta hal-platform hal-vlan hal-wifi avro-c "

CFLAGS_prepend += "-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/libnl3 "
CFLAGS_prepend += "-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/ "
CFLAGS_append = " \
    -I${STAGING_INCDIR}/trower-base64 \
    "

DEPENDS_append += " libunpriv"

CFLAGS += " -Wall -Werror -Wextra -Wno-implicit-function-declaration -Wno-type-limits -Wno-unused-parameter "

CFLAGS_append_dunfell = " -Wno-format-overflow -Wno-format-truncation -Wno-address-of-packed-member -Wno-tautological-compare -Wno-stringop-truncation "

SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/OneWifi;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=OneWifi"

SRCREV_OneWifi = "${AUTOREV}"
SRCREV_FORMAT = "OneWifi"
PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig systemd pythonnative breakpad-logmapper

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
LDFLAGS_remove_dunfell = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' -lsafec-3.5 ', '', d)}"
LDFLAGS_append_dunfell = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' -lsafec-3.5.1 ', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"

EXTRA_OECONF_append = " --disable-libwebconfig"
EXTRA_OECONF_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--enable-notify', '', d)}"
ISSYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}"
CFLAGS_append = " \
    -I${STAGING_INCDIR}/ccsp \
    -I=${includedir}/rbus \
"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'meshwifi', '-DENABLE_FEATURE_MESHWIFI', '', d)}"
LDFLAGS_append = " \
    -ltelemetry_msgsender \
    -lrbus \
    -lwifi_webconfig \
"
LDFLAGS_append += " -lprivilege"
#LDFLAGS_append = " -L${PKG_CONFIG_SYSROOT_DIR}/usr/opensync/lib -low -losw -lopensync"

do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/ccsp/wifi
    install -m 664 ${S}/scripts/process_monitor_atom.sh -t ${D}/usr/ccsp/wifi
    install -m 755 ${S}/scripts/br0_ip.sh -t ${D}/usr/ccsp/wifi
    install -m 755 ${S}/scripts/br106_addvlan.sh -t ${D}/usr/ccsp/wifi
    install -m 755 ${S}/scripts/copy_wifi_logs.sh -t ${D}/usr/ccsp/wifi
    install -m 755 ${S}/scripts/wifi_logupload.sh -t ${D}/usr/ccsp/wifi
    install -m 755 ${S}/scripts/wifi_self_heal_cron.sh -t ${D}/usr/ccsp/wifi
    install -m 755 ${S}/scripts/wifi_self_heal_command.sh -t ${D}/usr/ccsp/wifi
    install -m 755 ${S}/scripts/lfp.sh -t ${D}/usr/ccsp/wifi
    install -m 755 ${S}/scripts/aphealth.sh -t ${D}/usr/ccsp/wifi
    install -m 755 ${S}/scripts/aphealth_log.sh -t ${D}/usr/ccsp/wifi
    install -m 755 ${S}/scripts/wifivAPPercentage.sh -t ${D}/usr/ccsp/wifi
    # Only install services if meshwifi has been defined.
    if ${@bb.utils.contains('DISTRO_FEATURES', 'meshwifi', 'true', 'false', d)}; then
        install -m 755 ${S}/scripts/mesh_aclmac.sh -t ${D}/usr/ccsp/wifi
        install -m 755 ${S}/scripts/mesh_setip.sh -t ${D}/usr/ccsp/wifi
        install -m 755 ${S}/scripts/meshapcfg.sh -t ${D}/usr/ccsp/wifi
        install -m 755 ${S}/scripts/handle_mesh -t ${D}/usr/ccsp/wifi
        install -m 755 ${S}/scripts/mesh_status.sh -t ${D}/usr/ccsp/wifi
    fi

    install -m 775 ${S}/config/CcspWifi.cfg -t ${D}/usr/ccsp/wifi
    install -m 775 ${S}/config/CcspDmLib.cfg -t ${D}/usr/ccsp/wifi
    install -m 664 ${S}/config/WifiSingleClient.avsc -t ${D}/usr/ccsp/wifi
    install -m 664 ${S}/config/WifiSingleClientActiveMeasurement.avsc -t ${D}/usr/ccsp/wifi
    install -m 664 ${S}/config/rdkb-wifi.ovsschema -t ${D}/usr/ccsp/wifi
    install -m 755 ${D}/usr/bin/wifi_db_ovsh -t ${D}/usr/ccsp/wifi
    install -d ${D}/usr/include/ccsp
    install -d ${D}/usr/include/middle_layer_src
    install -d ${D}/usr/include/middle_layer_src/wifi
    install -m 644 ${S}/source/dml/tr_181/sbapi/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/include/tr_181/ml/*.h ${D}/usr/include/middle_layer_src/wifi

	if [ ${ISSYSTEMD} = "true" ]; then
    	install -d ${D}${systemd_unitdir}/system
    	install -D -m 755 ${S}/scripts/wifiTelemetrySetup.sh ${D}${exec_prefix}/ccsp/wifi/wifiTelemetrySetup.sh
    	install -D -m 0644 ${S}/scripts/systemd/wifi-telemetry.target ${D}${systemd_unitdir}/system/wifi-telemetry.target
    	install -D -m 0644 ${S}/scripts/systemd/wifi-telemetry-cron.service ${D}${systemd_unitdir}/system/wifi-telemetry-cron.service
	fi
}

do_install_append_mips (){
    install -d ${D}/usr/ccsp/wifi
    install -m 775 ${S}/config/CcspWifi.cfg -t ${D}/usr/ccsp/wifi
    install -m 775 ${S}/config/CcspDmLib.cfg -t ${D}/usr/ccsp/wifi
}

do_install_append_puma7 () {
    rm ${D}/usr/ccsp/wifi/br0_ip.sh
}

do_install_append_bcm3390() {
    rm ${D}/usr/ccsp/wifi/br0_ip.sh
}

PACKAGES =+ "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

FILES_${PN}-gtest = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/OneWifi_gtest.bin', '', d)} \
"

FILES_${PN} = "\
    ${bindir}/OneWifi \
    ${bindir}/wifi_ctrl \
    ${bindir}/wifi_webconfig_consumer \
    ${bindir}/wifi_api2 \
    ${libdir}/libwifi.so* \
    ${prefix}/ccsp/wifi/process_monitor_atom.sh \
    ${prefix}/ccsp/wifi/br0_ip.sh \
    ${prefix}/ccsp/wifi/br106_addvlan.sh \
    ${prefix}/ccsp/wifi/copy_wifi_logs.sh \
    ${prefix}/ccsp/wifi/wifi_logupload.sh \
    ${prefix}/ccsp/wifi/wifi_self_heal_cron.sh \
    ${prefix}/ccsp/wifi/wifi_self_heal_command.sh \
    ${prefix}/ccsp/wifi/lfp.sh \
    ${prefix}/ccsp/wifi/aphealth.sh \
    ${prefix}/ccsp/wifi/aphealth_log.sh \
    ${prefix}/ccsp/wifi/apshealth.sh \
    ${prefix}/ccsp/wifi/wifivAPPercentage.sh \
    ${prefix}/ccsp/wifi/mesh_aclmac.sh \
    ${prefix}/ccsp/wifi/mesh_setip.sh \
    ${prefix}/ccsp/wifi/meshapcfg.sh \
    ${prefix}/ccsp/wifi/handle_mesh \
    ${prefix}/ccsp/wifi/mesh_status.sh \
    ${prefix}/ccsp/wifi/CcspWifi.cfg \
    ${prefix}/ccsp/wifi/CcspDmLib.cfg \
    ${prefix}/ccsp/wifi/WifiSingleClient.avsc \
    ${prefix}/ccsp/wifi/WifiSingleClientActiveMeasurement.avsc \
    ${prefix}/ccsp/wifi/rdkb-wifi.ovsschema \
    ${prefix}/ccsp/wifi/wifi_db_ovsh \
"

FILES_${PN}-dbg = " \
    ${prefix}/ccsp/wifi/.debug \
    ${prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

SYSTEMD_SERVICE_${PN} += " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'wifi-telemetry.target', '', d)}"
SYSTEMD_SERVICE_${PN} += " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'wifi-telemetry-cron.service', '', d)}"

FILES_${PN}_append += " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${exec_prefix}/ccsp/wifi/wifiTelemetrySetup.sh', '', d)}"
FILES_${PN}_append += " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_unitdir}/system/wifi-telemetry.target', '', d)}"
FILES_${PN}_append += " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_unitdir}/system/wifi-telemetry-cron.service', '', d)}"

ERROR_QA_remove_morty = "la"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-OneWifi', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="gtest"
SKIP_MAIN_PKG="yes"
DOWNLOAD_ON_DEMAND="yes"
# Breakpad processname and logfile mapping
BREAKPAD_LOGMAPPER_PROCLIST = "OneWifi,log_agent"
BREAKPAD_LOGMAPPER_LOGLIST = "WiFilog.txt.0,WifiConsole.txt,wifihealth.txt"
