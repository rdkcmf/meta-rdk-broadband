SUMMARY = "CCSP CcspCMAgent component"
HOMEPAGE = "http://github.com/belvedere-yocto/CcspCMAgent"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "utopia ccsp-common-library hal-cm hal-dhcpv4c hal-ethsw hal-moca hal-mso_mgmt hal-mta hal-platform hal-vlan hal-wifi libunpriv"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' telemetry ', ' ', d)}"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' ruli ', ' ', d)}"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' cimplog ', ' ', d)}"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'WanFailOverSupportEnable', ' rbus ', " ", d)}"


require ccsp_common.inc

SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/CcspCMAgent;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=CcspCMAgent"

SRCREV_CcspCMAgent = "${AUTOREV}"
SRCREV_FORMAT = "CcspCMAgent"
PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pythonnative breakpad-logmapper

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
LDFLAGS_remove_dunfell = "${@bb.utils.contains('DISTRO_FEATURES', 'safec', '-lsafec-3.5', '', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'safec dunfell', ' -lsafec-3.5.1 ', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
CFLAGS_append =  "${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' -DCONFIG_CISCO_HOME_SECURITY ', '', d)}"

LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' -lpthread ', '', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' -lhal_platform ', '', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' -lrt ', '', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' -lcimplog ', '', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' -ltelemetry_msgsender ', '', d)}"

CFLAGS_prepend += " ${@bb.utils.contains('DISTRO_FEATURES', 'highsplit', '-D_CM_HIGHSPLIT_SUPPORTED_', '', d)}"
CFLAGS_prepend += " ${@bb.utils.contains('DISTRO_FEATURES', 'highsplit', ' -I=${includedir}/sysevent ', '',d)} "
LDFLAGS_append += " ${@bb.utils.contains('DISTRO_FEATURES', 'highsplit',' -lsysevent', '',d)}"

EXTRA_OECONF_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'WanFailOverSupportEnable', ' --enable-wanfailover ', '', d)}"


EXTRA_OECONF_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--enable-notify', '', d)}"
EXTRA_OECONF_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', '--enable-wanmgr', '', d)}"

CFLAGS_append = " \
    -I=${includedir}/dbus-1.0 \
    -I=${libdir}/dbus-1.0/include \
    -I=${includedir}/ccsp \
    -I${STAGING_INCDIR}/cimplog \
    "
CFLAGS_append  = " ${@bb.utils.contains('DISTRO_FEATURES', 'WanFailOverSupportEnable', ' -I=${includedir}/rbus ', '', d)}"

LDFLAGS_append = " \
    -ldbus-1 \
    -lprivilege \
    -lsyscfg \
    "
LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'WanFailOverSupportEnable', ' -lrbus ', '', d)}"

CFLAGS += " -Wall -Werror -Wextra "

do_compile_prepend () {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'highsplit', 'true', 'false', d)}; then
        sed -i '2i <?define _CM_HIGHSPLIT_SUPPORTED_=True?>' ${S}/config-arm/TR181-CM.XML
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', 'true', 'false', d)}; then
        sed -i '2i <?define FEATURE_RDKB_WAN_MANAGER=True?>' ${S}/config-arm/TR181-CM.XML
    fi
     (python ${STAGING_BINDIR_NATIVE}/dm_pack_code_gen.py ${S}/config-arm/TR181-CM.XML ${S}/source/CMAgentSsp/dm_pack_datamodel.c)
}

do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/ccsp/cm
    ln -sf /usr/bin/CcspCMAgentSsp ${D}${prefix}/ccsp/cm/CcspCMAgentSsp
    install -d ${D}/usr/include/ccsp
    install -d ${D}/usr/include/middle_layer_src
    install -d ${D}/usr/include/middle_layer_src/cm
    install -m 644 ${S}/source/TR-181/middle_layer_src/*.h ${D}/usr/include/middle_layer_src/cm
    install -m 644 ${S}/source/TR-181/include/*.h ${D}/usr/include/ccsp
}

PACKAGES += "${PN}-ccsp"

PACKAGES =+ "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

FILES_${PN}-gtest = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/CcspCMAgentSsp_gtest.bin', '', d)} \
"

FILES_${PN}-ccsp = " \
    ${prefix}/ccsp/cm/CcspCMAgentSsp \
    ${prefix}/ccsp/cm/CcspCMDM.cfg \
    ${prefix}/ccsp/cm/CcspCM.cfg \
"

FILES_${PN}-dbg = " \
    ${prefix}/ccsp/cm/.debug \
    ${prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-CcspCMAgentSsp', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="gtest"
SKIP_MAIN_PKG="yes"
DOWNLOAD_ON_DEMAND="yes"
# Breakpad processname and logfile mapping
BREAKPAD_LOGMAPPER_PROCLIST = "CcspCMAgentSsp"
BREAKPAD_LOGMAPPER_LOGLIST = "CMlog.txt.0,cmconsole.log,cmevent.log"
