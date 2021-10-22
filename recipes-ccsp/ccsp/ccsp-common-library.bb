SUMMARY = "CCSP libccsp_common component"
HOMEPAGE = "http://github.com/belvedere-yocto/CcspCommonLibrary"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=19774cd4dd519f099bc404798ceeab19"

DEPENDS = "dbus openssl rbus rbus-core"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"
DEPENDS_class-native = ""

RDEPENDS_${PN}_append_dunfell = " bash"

require ccsp_common.inc

SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/CcspCommonLibrary;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH}"

SRC_URI_append_dunfell = " file://0001-DBusLoop-SSL_state-TLS_ST_OK.patch \
			   file://0001-SSLeay_add_all_algorithms-remove-in-openssl-1.1.patch"

SRCREV = "${AUTOREV}"
SRCREV_FORMAT = "${AUTOREV}"
PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools systemd pkgconfig

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"

CFLAGS += " \
    -D_GNU_SOURCE -D__USE_XOPEN \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/rbus \
    -I${STAGING_INCDIR}/rtmessage \
    "

CFLAGS += " -Wall -Werror -Wextra "

CFLAGS_append_dunfell = " -Wno-restrict -Wno-format-truncation -Wno-format-overflow -Wno-cast-function-type -Wno-unused-function -Wno-implicit-fallthrough "

LDFLAGS += " \
    -ldbus-1 \
    -lrbus-core \
    -lrtMessage \
    -lrbus \
    "

do_configure_class-native () {
    echo "Configure is skipped"
}

do_compile_class-native () {
    echo "Compile is skipped"
}

do_install_append_class-target () {
    install -d ${D}/usr/include/ccsp
    install -d ${D}/usr/include/ccsp/linux
    install -m 644 ${S}/source/debug_api/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/util_api/ansc/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/util_api/asn.1/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/util_api/http/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/util_api/slap/components/SlapVarConverter/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/util_api/stun/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/util_api/tls/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/util_api/web/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/cosa/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/cosa/package/slap/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/cosa/package/system/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/cosa/include/linux/*.h ${D}/usr/include/ccsp/linux
    install -m 644 ${S}/source/cosa/include/linux/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/ccsp/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/ccsp/custom/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/ccsp/components/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/ccsp/components/common/MessageBusHelper/include/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/ccsp/components/common/PoamIrepFolder/*.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/dm_pack/dm_pack_create_func.h ${D}/usr/include/ccsp
    install -m 644 ${S}/source/dm_pack/dm_pack_xml_helper.h ${D}/usr/include/ccsp

    # Config files and scripts
    install -d ${D}/usr/ccsp
    install -d ${D}/usr/ccsp/cm
    install -d ${D}/usr/ccsp/mta
    install -d ${D}/usr/ccsp/pam
    install -d ${D}/usr/ccsp/tr069pa
    install -D -m 755 ${S}/scripts/cosa_stop.sh ${D}/usr/ccsp/cosa_stop.sh
    install -D -m 755 ${S}/scripts/cosa ${D}${sysconfdir}/ccsp/cosa

    # RBUS related scripts
    install -d ${D}/lib/rdk
    install -m 777 ${S}/scripts/rbus_termination_handler.sh ${D}/lib/rdk/rbus_termination_handler.sh
    install -m 777 ${S}/scripts/rbusFlagSync.sh ${D}/usr/ccsp/rbusFlagSync.sh
    install -m 777 ${S}/scripts/rbus_status_logger.sh ${D}/usr/ccsp/rbus_status_logger.sh
    install -m 777 ${S}/scripts/rbus_rfc_handler.sh ${D}/lib/rdk/rbus_rfc_handler.sh
}

do_install_class-native () {
    install -d ${D}${bindir}
    install -m 644 ${S}/source/dm_pack/dm_pack_code_gen.py ${D}${bindir}
}
do_install_append_broadband() {
        install -d ${D}${systemd_unitdir}/system/CcspMtaAgentSsp.service.d
        install -D -m 644 ${S}/systemd_units/CcspMtaAgentSsp.conf ${D}${systemd_unitdir}/system/CcspMtaAgentSsp.service.d/CcspMtaAgentSsp.conf
}

FILES_${PN}_append += "${systemd_unitdir}/system/CcspMtaAgentSsp.service.d/CcspMtaAgentSsp.conf"

PACKAGES =+ "ccsp-common-startup"


FILES_ccsp-common-startup = " \
    ${exec_prefix}/ccsp/basic.conf \
    ${exec_prefix}/ccsp/cli_start.sh \
    ${exec_prefix}/ccsp/cosa_*.sh \
    ${exec_prefix}/ccsp/ccsp_restart.sh \
    ${exec_prefix}/ccsp/ccsp_msg.cfg \
    ${exec_prefix}/ccsp/cm/ccsp_msg.cfg \
    ${exec_prefix}/ccsp/mta/ccsp_msg.cfg \
    ${exec_prefix}/ccsp/pam/ccsp_msg.cfg \
    ${exec_prefix}/ccsp/tr069pa/ccsp_msg.cfg \
"

FILES_${PN}-dbg = " \
    ${exec_prefix}/ccsp/.debug \
    ${exec_prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

FILES_${PN}_append = " \
                     /usr/ccsp/rbusFlagSync.sh \
                     /usr/ccsp/rbus_status_logger.sh \
                     /lib/rdk/rbus_termination_handler.sh \
                     /lib/rdk/rbus_rfc_handler.sh \
                      "

FILES_${PN}-native = " ${bindir}/dm_pack_code_gen.py "

BBCLASSEXTEND = "native"

DEPENDS_remove_class-native = " safec-native"

PACKAGES =+ " ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${PN}-gtest', '', d)}"

FILES_${PN}-gtest = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', '${bindir}/CcspCommonLibrary_gtest.bin', '', d)} \
"

DOWNLOAD_APPS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'gtestapp-CcspCommonLibrary', '', d)}"
inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', ' gtest libccsp-common-gtest', '', d)}"
SKIP_MAIN_PKG="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
DOWNLOAD_ON_DEMAND="${@bb.utils.contains('DISTRO_FEATURES', 'gtestapp', 'yes', 'no', d)}"
