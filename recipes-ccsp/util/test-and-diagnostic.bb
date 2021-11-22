SUMMARY = "CCSP test and diagnostice utilities."
HOMEPAGE = "http://github.com/belvedere-yocto/TestAndDiagnostic"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library utopia hal-cm hal-dhcpv4c hal-ethsw hal-moca hal-mso_mgmt hal-mta hal-platform hal-vlan hal-wifi"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
require recipes-ccsp/ccsp/ccsp_common.inc
SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/TestAndDiagnostic;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=TestAndDiagnostic"

SRCREV_TestAndDiagnostic = "${AUTOREV}"
SRCREV_FORMAT = "TestAndDiagnostic"
PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

CFLAGS += " -Wall -Werror -Wextra -Wno-pointer-sign -Wno-sign-compare -Wno-type-limits -Wno-unused-parameter "

RDEPENDS_${PN}_append_dunfell = " bash"
RDEPENDS_${PN}-ccsp_append_dunfell += " bash"

inherit autotools pythonnative breakpad-logmapper

CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I${STAGING_INCDIR}/utapi \
    -I${STAGING_INCDIR}/utctx \
    -I${STAGING_INCDIR}/ulog \
    -I${STAGING_INCDIR}/syscfg \
    "

EXTRA_OECONF_append = "--enable-mta"

LDFLAGS_append = " \
    -ldbus-1 \
    "

# Fan & Thermal Control Feature
HASTHERMAL = "${@bb.utils.contains('DISTRO_FEATURES', 'thermalctrl', 'true', 'false', d)}"
CFLAGS_append += "${@bb.utils.contains('DISTRO_FEATURES', 'thermalctrl','-DFAN_THERMAL_CTR','',d)}"
LDFLAGS_append += "${@bb.utils.contains('DISTRO_FEATURES', 'thermalctrl',' -lhal_platform','',d)}"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"

do_compile_prepend () {
    (python ${STAGING_BINDIR_NATIVE}/dm_pack_code_gen.py ${S}/config/TestAndDiagnostic_arm.XML ${S}/source/TandDSsp/dm_pack_datamodel.c)
}
do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/ccsp/tad
    install -d ${D}/usr/include/ccsp
    ln -sf /usr/bin/CcspTandDSsp ${D}/usr/ccsp/tad/CcspTandDSsp
    install -m 644 ${S}/source/dmltad/diag*.h ${D}/usr/include/ccsp/
   
    install -m 755 ${S}/source/CpuMemFrag/cpumemfrag_cron.sh ${D}/usr/ccsp/tad/cpumemfrag_cron.sh
    install -m 755 ${S}/source/CpuMemFrag/log_buddyinfo.sh ${D}/usr/ccsp/tad/log_buddyinfo.sh

    if [ ${HASTHERMAL} = "true" ]; then
    	install -m 755 ${S}/source/ThermalCtrl/check_fan.sh ${D}/usr/ccsp/tad/check_fan.sh
    fi
    if ${@bb.utils.contains('DISTRO_FEATURES', 'bci', 'true', 'false', d)}; then
        if [ "${MACHINE_IMAGE_NAME}" = "CGA4332COM" ] || [ "${MACHINE_IMAGE_NAME}" = "CGA4131COM" ]; then
            install -m 0755 ${S}/source/StaticInfo/log_staticIP_client_info.sh ${D}/usr/ccsp/tad/log_staticIP_client_info.sh
        fi
    fi
}

PACKAGES += "${PN}-ccsp"

PACKAGES =+ "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

FILES_${PN}-gtest = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/CcspTandDSsp_gtest.bin', '', d)} \
"

FILES_${PN}-ccsp = " \
    ${libdir}/libdiagnostic.so.* \
    ${libdir}/libdmltad.so.* \
    ${prefix}/ccsp/Sub64 \
    ${bindir}/Sub64 \
    ${bindir}/CcspTandDSsp \
    /fss/gw/usr/ccsp/* \
    ${prefix}/ccsp/tad/*.sh \
    ${prefix}/ccsp/tad/CcspTandDSsp \
    ${sbindir}/* \
"

FILES_${PN}-dbg = " \
    ${prefix}/ccsp/tad/.debug \
    ${prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-CcspTandDSsp', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtest', '', d)}"
SKIP_MAIN_PKG="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
DOWNLOAD_ON_DEMAND="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
# Breakpad processname and logfile mapping
BREAKPAD_LOGMAPPER_PROCLIST = "CcspTandDSsp"
BREAKPAD_LOGMAPPER_LOGLIST = "TDMlog.txt.0"
