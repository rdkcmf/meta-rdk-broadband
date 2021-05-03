SUMMARY = "CCSP PandMSsp component"
HOMEPAGE = "http://github.com/belvedere-yocto/CcspPandM"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

RPROVIDES_${PN} = "ccsp-p-and-m"

DEPENDS = "ccsp-common-library webconfig-framework ccsp-lm-lite telemetry ccsp-hotspot"
DEPENDS_append = " utopia hal-cm hal-dhcpv4c hal-ethsw hal-moca hal-mso_mgmt hal-mta hal-platform hal-vlan hal-wifi curl ccsp-misc ccsp-hotspot cjson libsyswrapper cjson trower-base64 msgpack-c nanomsg cimplog wrp-c libparodus"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"

DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'fwupgrade_manager', ' hal-fwupgrade', '',d)}"

RDEPENDS_${PN}_append = " cjson trower-base64 msgpack-c nanomsg cimplog wrp-c libparodus "

RDEPENDS_${PN}-ccsp_append_dunfell = " bash"

require ccsp_common.inc

CFLAGS += " -Wall -Werror -Wextra -Wno-shift-negative-value"

CFLAGS_append_dunfell = " -Wno-format-truncation -Wno-format-overflow -Wno-deprecated-declarations -Wno-sizeof-pointer-memaccess -Wno-memset-elt-size -Wno-maybe-uninitialized "

SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/CcspPandM;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=CcspPandM"

SRC_URI_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', 'file://0001-disable-fwupgrade-dm.patch', '', d)}"

SRC_URI_append_dunfell = " file://0001-openssl-1.1.x-compatibility-in-HMAC-functions.patch"
SRC_URI_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', 'file://0002-disable-dhcp-clients-tr181-dml-in-PandM.patch', '', d)}"
SRC_URI_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', 'file://0003-disable-ppp-tr181-dml-in-PandM.patch', '', d)}"

SRCREV_CcspPandM = "${AUTOREV}"
SRCREV_FORMAT = "CcspPandM"
PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pythonnative

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
LDFLAGS_remove_dunfell = "${@bb.utils.contains('DISTRO_FEATURES', 'safec', '-lsafec-3.5', '', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'safec dunfell', ' -lsafec-3.5.1 ', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'wbCfgTestApp', '-DWEBCFG_TEST_SIM', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'ethstats', '-DETH_STATS_ENABLED', '', d)}"
LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', '-lnanomsg', '', d)}"
LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'fwupgrade_manager', '-lfw_upgrade', '', d)}"

CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I${STAGING_INCDIR}/utapi \
    -I${STAGING_INCDIR}/utctx \
    -I${STAGING_INCDIR}/ulog \
    -I${STAGING_INCDIR}/syscfg \
    -I${STAGING_INCDIR}/wrp-c \
    -I${STAGING_INCDIR}/cimplog \
    -I${STAGING_INCDIR}/nanomsg \
    -I${STAGING_INCDIR}/trower-base64 \
    -I${STAGING_INCDIR}/msgpackc \
    -I${STAGING_INCDIR}/libparodus \
    -I${STAGING_INCDIR}/cjson \
    "

EXTRA_OECONF_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--enable-notify', '', d)}"

CFLAGS_append = " -DCONFIG_VENDOR_CUSTOMER_COMCAST -DCONFIG_INTERNET2P0 -DCONFIG_CISCO_HOTSPOT"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'bci', '-DCISCO_CONFIG_TRUE_STATIC_IP -DCISCO_CONFIG_DHCPV6_PREFIX_DELEGATION -DCONFIG_CISCO_TRUE_STATIC_IP -D_BCI_FEATURE_REQ', '', d)}"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'meshwifi', '-DENABLE_FEATURE_MESHWIFI', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'dslite', '-DDSLITE_FEATURE_SUPPORT', '', d)}" 
LDFLAGS_append = " \
    -ldbus-1 \
    -lutctx \
    -lutapi \
    -lm \
    -lcjson \
    -llibparodus \
    -lnanomsg \
    -lwrp-c \
    -lmsgpackc \
    -ltrower-base64 \
    -lm \
    -lcimplog \
    -lpthread \
    -lrt \
    -lsysevent \
    -ltelemetry_msgsender \
"

LDFLAGS_append_dunfell = " -lsyscfg"

do_compile_prepend () {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'bci', 'true', 'false', d)}; then
		(python ${STAGING_BINDIR_NATIVE}/dm_pack_code_gen.py ${S}/config-arm/TR181-USGv2_bci.XML ${S}/source/PandMSsp/dm_pack_datamodel.c)
	else
		(python ${STAGING_BINDIR_NATIVE}/dm_pack_code_gen.py ${S}/config-arm/TR181-USGv2.XML ${S}/source/PandMSsp/dm_pack_datamodel.c)
	fi

}
do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/ccsp/pam
    install -d ${D}${sysconfdir}
    install -m 755 ${S}/scripts/unique_telemetry_id.sh ${D}/usr/ccsp/pam/unique_telemetry_id.sh
    install -m 755 ${S}/scripts/launch_tr69.sh ${D}/usr/ccsp/pam/launch_tr69.sh
    install -d ${D}/usr/include/ccsp
    install -d ${D}/usr/include/middle_layer_src
    install -d ${D}/usr/include/middle_layer_src/pam
    install -m 644 ${S}/source/TR-181/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/TR-181/middle_layer_src/*.h ${D}/usr/include/middle_layer_src/pam
    install -m 755 ${S}/arch/intel_usg/boards/arm_shared/scripts/partners_defaults.json ${D}/etc/partners_defaults.json
    install -m 755 ${S}/arch/intel_usg/boards/arm_shared/scripts/rfcDefaults.json ${D}/etc/rfcDefaults.json
    install -m 755 ${S}/arch/intel_usg/boards/arm_shared/scripts/ScheduleAutoReboot.sh ${D}/etc/ScheduleAutoReboot.sh
    install -m 755 ${S}/arch/intel_usg/boards/arm_shared/scripts/AutoReboot.sh ${D}/etc/AutoReboot.sh
    install -m 755 ${S}/arch/intel_usg/boards/arm_shared/scripts/RebootCondition.sh ${D}/etc/RebootCondition.sh
}

do_install_append_qemux86 () {
   #Config files and scripts
    install -m 644 ${S}/config-pc/COSAXcalibur.XML -t ${D}/usr/ccsp/pam
}

PACKAGES += "${PN}-ccsp"

FILES_${PN}-ccsp = " \
    ${prefix}/ccsp/pam/CcspDmLib.cfg  \
    ${prefix}/ccsp/pam/CcspPam.cfg  \
    ${prefix}/ccsp/pam/email_notification_monitor.sh  \
    ${prefix}/ccsp/pam/unique_telemetry_id.sh  \
    ${prefix}/ccsp/pam/calc_random_time_to_reboot_dev.sh  \
    ${prefix}/ccsp/pam/network_response.sh  \
    ${prefix}/ccsp/pam/redirect_url.sh \
    ${prefix}/ccsp/pam/revert_redirect.sh \
    ${prefix}/ccsp/pam/whitelist.sh \
    ${prefix}/ccsp/pam/restart_services.sh \
    ${prefix}/ccsp/pam/moca_status.sh \
    ${prefix}/ccsp/pam/erouter0_ip_sync.sh \
    ${prefix}/ccsp/pam/launch_tr69.sh \
    ${prefix}/ccsp/pam/ScheduleAutoReboot.sh \
    ${prefix}/ccsp/pam/AutoReboot.sh \
    ${prefix}/ccsp/pam/RebootCondition.sh \
    /fss/gw/usr/sbin/ip \
    /fss/gw/usr/ccsp/pam/mapping.txt \
"

FILES_${PN}-dbg = " \
    ${prefix}/ccsp/pam/.debug \
    ${prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"
FILES_${PN}-ccsp_append_qemux86 = " \
    ${prefix}/ccsp/pam/COSAXcalibur.XML \
"


