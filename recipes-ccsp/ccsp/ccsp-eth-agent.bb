SUMMARY = "This receipe provides test component support."
SECTION = "console/utils"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

HASWANMANAGER = "${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', 'true', 'false', d)}"
DEPENDS = "ccsp-common-library dbus utopia hal-ethsw hal-platform curl ccsp-lm-lite cimplog libunpriv"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' json-hal-lib', '',d)}"

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
LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' -ljson_hal_client -ljson-c', '', d)}"
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
    -I${STAGING_INCDIR}/cimplog \
    "

LDFLAGS_append = " \
    -lccsp_common \
    -ldbus-1 \
    -lutctx \
    -lutapi \
    -lcimplog \
    -lprivilege \
    "
LDFLAGS_append_dunfell = " -lsyscfg"
do_compile_prepend () {
    (python ${STAGING_BINDIR_NATIVE}/dm_pack_code_gen.py ${S}/config/TR181-EthAgent.xml ${S}/source/EthSsp/dm_pack_datamodel.c)
}

do_install_append () {
    # Config files and scripts
    install -d ${D}${exec_prefix}/ccsp/ethagent
    install -m 644 ${S}/config/TR181-EthAgent.xml ${D}${exec_prefix}/ccsp/ethagent/TR181-EthAgent.xml
    if [ ${HASWANMANAGER} = "true" ]; then
        install -d ${D}${sysconfdir}/rdk/conf
        install -m 644 ${S}/config/eth_manager_conf.json ${D}${sysconfdir}/rdk/conf
        # Json Schema file.
        install -d ${D}/${sysconfdir}/rdk/schemas
        install -m 644 ${S}/hal_schema/eth_hal_schema.json ${D}/${sysconfdir}/rdk/schemas
    fi
}
FILES_${PN} += " ${exec_prefix}/ccsp/ethagent"
FILES_${PN} += " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', '${sysconfdir}/rdk/conf/eth_manager_conf.json','', d)}"
FILES_${PN} += " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', '${sysconfdir}/rdk/schemas/eth_hal_schema.json','', d)}"
