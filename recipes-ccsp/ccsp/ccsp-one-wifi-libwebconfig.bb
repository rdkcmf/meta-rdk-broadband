DEPENDS_append = " libunpriv "
LDFLAGS_append = " \
                 -lprivilege \
                 "

SUMMARY = "CCSP OneWifi component"
HOMEPAGE = "http://github.com/belvedere-yocto/OneWifi"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=042d68aa6c083a648f58bb8d224a4d31"

DEPENDS = "ccsp-common-library webconfig-framework hal-cm hal-dhcpv4c hal-ethsw hal-moca hal-mso_mgmt hal-mta hal-platform hal-vlan hal-wifi utopia libparodus avro-c telemetry libsyswrapper libev rbus libnl opensync-headers"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'systemd', '', d)}"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

CFLAGS_prepend += "-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/opensync_headers "
CFLAGS_prepend += "-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/libnl3 "
CFLAGS_prepend += "-I${PKG_CONFIG_SYSROOT_DIR}/usr/include/ "
CFLAGS_append = " \
    -I${STAGING_INCDIR}/trower-base64 \
    "
DEPENDS_append += " libunpriv"

RDEPENDS_${PN}_append = " libparodus "

CFLAGS += " -Wall -Werror -Wextra -Wno-implicit-function-declaration -Wno-type-limits -Wno-unused-parameter "

CFLAGS += "-DWIFI_HAL_VERSION_3"

CFLAGS_append_dunfell = " -Wno-format-overflow -Wno-format-truncation -Wno-address-of-packed-member -Wno-tautological-compare -Wno-stringop-truncation "

require ccsp_common.inc
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

EXTRA_OECONF_append = " --enable-libwebconfig"
EXTRA_OECONF_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '--enable-notify', '', d)}"
ISSYSTEMD = "${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'true', 'false', d)}"
CFLAGS_append = " \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I${STAGING_INCDIR}/libparodus \
    -I=${includedir}/rbus \
"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'meshwifi', '-DENABLE_FEATURE_MESHWIFI', '', d)}"

LDFLAGS_append = " \
    -ldbus-1 \
    -llibparodus \
    -ltelemetry_msgsender \
    -lrbus \
"
LDFLAGS_append += " -lprivilege"

do_compile() {
    oe_runmake -C source/webconfig
}

do_install() {
    oe_runmake -C source/webconfig DESTDIR=${D} install

    install -d ${D}/usr/ccsp/wifi
    install -m 664 ${S}/config/rdkb-wifi.ovsschema -t ${D}/usr/ccsp/wifi
    install -d ${D}/usr/include/ccsp
    install -d ${D}/usr/include/middle_layer_src
    install -d ${D}/usr/include/middle_layer_src/wifi
    #install -m 644 ${S}/source/dml/tr_181/sbapi/*.h ${D}/usr/include/ccsp
    #install -m 644 ${S}/include/tr_181/ml/*.h ${D}/usr/include/middle_layer_src/wifi
    install -m 644 ${S}/include/webconfig_external_proto_ovsdb.h  ${D}/usr/include/ccsp
    install -m 644 ${S}/include/webconfig_external_proto.h  ${D}/usr/include/ccsp
    install -m 644 ${S}/include/webconfig_external_proto_tr181.h  ${D}/usr/include/ccsp
    install -m 644 ${S}/include/wifi_webconfig.h       ${D}/usr/include/ccsp
    install -m 644 ${S}/include/wifi_base.h       ${D}/usr/include/ccsp
    install -m 644 ${S}/source/utils/collection.h ${D}/usr/include/ccsp
}

FILES_${PN} = "\
    ${libdir}/libwifi.so* \
"

FILES_${PN}-dbg = " \
    ${prefix}/ccsp/wifi/.debug \
    ${prefix}/src/debug \
    ${libdir}/.debug \
"

ERROR_QA_remove_morty = "la"

inherit comcast-package-deploy
CUSTOM_PKG_EXTNS="gtest"
SKIP_MAIN_PKG="yes"
DOWNLOAD_ON_DEMAND="yes"
# Breakpad processname and logfile mapping
