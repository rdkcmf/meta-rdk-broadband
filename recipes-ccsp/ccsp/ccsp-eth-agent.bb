SUMMARY = "This receipe provides test component support."
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library dbus utopia hal-ethsw hal-platform curl ccsp-lm-lite libunpriv"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

require ccsp_common.inc

SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/CcspEthAgent;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=CcspEthAgent"
CFLAGS += " -Wall -Werror -Wextra "
CFLAGS_append_dunfell = " -Wno-format-truncation "

S = "${WORKDIR}/git"

PV = "${RDK_RELEASE}+git${SRCPV}"
SRCREV = "${AUTOREV}"
SRCREV_FORMAT = "${AUTOREV}"

inherit autotools pythonnative

PACKAGECONFIG ?= "dropearly"
PACKAGECONFIG[dropearly] = "--enable-dropearly,--disable-dropearly"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
LDFLAGS_remove_dunfell = "${@bb.utils.contains('DISTRO_FEATURES', 'safec', '-lsafec-3.5', '', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'safec dunfell', ' -lsafec-3.5.1 ', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
CFLAGS_append  = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' -DFEATURE_RDKB_WAN_MANAGER', '', d)}"
LDFLAGS_append_dunfell = " -lrt"

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

LDFLAGS_append = " \
    -lccsp_common \
    -ldbus-1 \
    -lutctx \
    -lutapi \
    -lrt \
    -lprivilege \
    "
do_compile_prepend () {
	if ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', 'true', 'false', d)}; then
    		sed -i '2i <?define FEATURE_RDKB_WAN_MANAGER=True?>' ${S}/config/TR181-EthAgent.xml
   	fi
    (python ${STAGING_BINDIR_NATIVE}/dm_pack_code_gen.py ${S}/config/TR181-EthAgent.xml ${S}/source/EthSsp/dm_pack_datamodel.c)
}

do_install_append () {
    # Config files and scripts
    install -d ${D}${exec_prefix}/ccsp/ethagent
    install -m 644 ${S}/config/TR181-EthAgent.xml ${D}${exec_prefix}/ccsp/ethagent/TR181-EthAgent.xml
}

PACKAGES += "${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

FILES_${PN}-gtest = "\
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/CcspEthAgent_gtest.bin', '', d)} \
"

FILES_${PN} =" \
        ${libdir}/systemd \
        ${bindir}/CcspEthAgent \
"

FILES_${PN} += " ${exec_prefix}/ccsp/ethagent"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-CcspEthAgent', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="gtest"
SKIP_MAIN_PKG="yes"
DOWNLOAD_ON_DEMAND="yes"
