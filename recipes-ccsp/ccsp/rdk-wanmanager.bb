SUMMARY = "RDK WAN Manager component"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library hal-cm dbus rdk-logger utopia hal-dhcpv4c libunpriv ccsp-misc"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', 'nanomsg', '', d)}"

require ccsp_common.inc

SRC_URI ="${RDKB_CCSP_ROOT_GIT}/RdkWanManager/generic;protocol=${RDK_GIT_PROTOCOL};branch=${CCSP_GIT_BRANCH};name=WanManager"

SRCREV_WanManager = "${AUTOREV}"
SRCREV_FORMAT = "WanManager"

PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig pythonnative

CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I ${STAGING_INCDIR}/syscfg \
    -I ${STAGING_INCDIR}/sysevent \
    "
CFLAGS_append  = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', '-DFEATURE_RDKB_WAN_MANAGER', '', d)}"
LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', '-lnanomsg', '', d)}"
CFLAGS_append  = " ${@bb.utils.contains('DISTRO_FEATURES', 'WanFailOverSupportEnable', '-DRBUS_BUILD_FLAG_ENABLE', '', d)}"

LDFLAGS += " -lprivilege"

do_compile_prepend () {
    if ${@bb.utils.contains('DISTRO_FEATURES', 'WanFailOverSupportEnable', 'true', 'false', d)}; then
    sed -i '2i <?define RBUS_BUILD_FLAG_ENABLE=True?>' ${S}/config/RdkWanManager.xml
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'RbusBuildFlagEnable', 'true', 'false', d)}; then
    sed -i '2i <?define RBUS_BUILD_FLAG_ENABLE=True?>' ${S}/config/RdkWanManager.xml
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', 'true', 'false', d)}; then
        (python ${STAGING_BINDIR_NATIVE}/dm_pack_code_gen.py ${S}/config/RdkWanManager.xml ${S}/source/WanManager/dm_pack_datamodel.c)
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'WanFailOverSupportEnable', 'true', 'false', d)}; then
    sed -i '2i <?define RBUS_BUILD_FLAG_ENABLE=True?>' ${S}/config/RdkWanManager.xml
    fi

    if ${@bb.utils.contains('DISTRO_FEATURES', 'RbusBuildFlagEnable', 'true', 'false', d)}; then
    sed -i '2i <?define RBUS_BUILD_FLAG_ENABLE=True?>' ${S}/config/RdkWanManager.xml
    fi

}

do_install_append () {
    # Config files and scripts
    install -d ${D}${exec_prefix}/rdk/wanmanager
    ln -sf ${bindir}/wanmanager ${D}${exec_prefix}/rdk/wanmanager/wanmanager
    ln -sf ${bindir}/netmonitor ${D}${exec_prefix}/rdk/wanmanager/netmonitor
    install -m 644 ${S}/config/RdkWanManager.xml ${D}/usr/rdk/wanmanager
}


FILES_${PN} = " \
   ${exec_prefix}/rdk/wanmanager/wanmanager \
   ${exec_prefix}/rdk/wanmanager/netmonitor \
   ${exec_prefix}/rdk/wanmanager/RdkWanManager.xml \
   ${bindir}/* \
"

FILES_${PN}-dbg = " \
    ${exec_prefix}/rdk/wanmanager/.debug \
    /usr/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"
