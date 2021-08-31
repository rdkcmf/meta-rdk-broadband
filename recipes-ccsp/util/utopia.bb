SUMMARY = "CCSP Utopia"
HOMEPAGE = "http://github.com/belvedere-yocto/Utopia"

LICENSE = "Apache-2.0 & BSD-3-Clause"
LIC_FILES_CHKSUM = "file://LICENSE;md5=baa21dec03307f641a150889224a157f"


DEPENDS = "ccsp-common-library hal-cm hal-dhcpv4c hal-ethsw hal-moca hal-mso_mgmt hal-mta hal-platform hal-vlan hal-wifi zlib dbus libnetfilter-queue libupnp cjson halinterface libevent libsyswrapper"
DEPENDS_append_libc-musl = " libtirpc"
DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' nanomsg ', ' ', d)}"

DEPENDS_append_dunfell = " libtirpc libsyswrapper"

RDEPENDS_${PN}_append_dunfell = " bash"

require recipes-ccsp/ccsp/ccsp_common.inc
SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/Utopia;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=Utopia"

SRCREV_Utopia = "${AUTOREV}"

SRCREV_FORMAT = "Utopia"

PV = "${RDK_RELEASE}"

S = "${WORKDIR}/git"

inherit autotools useradd update-alternatives pkgconfig

DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' safec', " ", d)}"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"
LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
DEPENDS_remove_class-native = " safec-native"
CFLAGS += " -Wall -Werror -Wextra -Wno-pointer-sign -Wno-sign-compare -Wno-deprecated-declarations -Wno-type-limits -Wno-unused-parameter -Wno-return-local-addr "

CFLAGS += " \
    -I${STAGING_INCDIR}/ccsp \
    -DCONFIG_BUILD_TRIGGER \
    "

CFLAGS_append_dunfell = " -I${STAGING_INCDIR}/tirpc -Wno-cast-function-type -Wno-address-of-packed-member "

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'bci', '-DCISCO_CONFIG_TRUE_STATIC_IP -DCISCO_CONFIG_DHCPV6_PREFIX_DELEGATION', '', d)}"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'meshwifi', '-DENABLE_FEATURE_MESHWIFI', '', d)}"

CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'dslite', '-DDSLITE_FEATURE_SUPPORT', '', d)}"
LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', '-lnanomsg', '', d)}"

CFLAGS_append_libc-musl = " -I${STAGING_INCDIR}/tirpc"
LDFLAGS_append_libc-musl = " -ltirpc"

LDFLAGS_append_dunfell = " -ltirpc -lrt"

do_install_append () {
#    install -D -m 0644 ${S}/source/include/autoconf.h ${D}${includedir}/utctx/autoconf.h
    install -D -m 0644 ${S}/source/ulog/ulog.h ${D}${includedir}/ulog/ulog.h
    install -D -m 0644 ${S}/source/utapi/lib/utapi.h ${D}${includedir}/utapi/utapi.h
    install -D -m 0644 ${S}/source/utapi/lib/utapi_wlan.h ${D}${includedir}/utapi/utapi_wlan.h
    install -D -m 0644 ${S}/source/utapi/lib/utapi_util.h ${D}${includedir}/utapi/utapi_util.h
    install -D -m 0644 ${S}/source/utctx/lib/utctx.h ${D}${includedir}/utctx/utctx.h
    install -D -m 0644 ${S}/source/utctx/lib/utctx_api.h ${D}${includedir}/utctx/utctx_api.h
    install -D -m 0644 ${S}/source/utctx/lib/utctx_rwlock.h ${D}${includedir}/utctx/utctx_rwlock.h
    install -D -m 0644 ${S}/source/syscfg/lib/syscfg.h ${D}${includedir}/syscfg/syscfg.h
    install -D -m 0644 ${S}/source/sysevent/lib/sysevent.h ${D}${includedir}/sysevent/sysevent.h
    install -D -m 0644 ${S}/source/sysevent/lib/libsysevent_internal.h ${D}${includedir}/sysevent/libsysevent_internal.h
    install -D -m 0644 ${S}/source/utapi/lib/utapi_tr_dhcp.h ${D}${includedir}/utapi/utapi_tr_dhcp.h
    install -m 0644 ${S}/source/utapi/lib/*.h ${D}${includedir}/utapi/
    install -d ${D}${sysconfdir}/cron

    install -d ${D}${sysconfdir}/cron/cron.every5minute
    install -m 444 ${WORKDIR}/udhcpc.vendor_specific  ${D}${sysconfdir}/udhcpc.vendor_specific
    install -m 755 ${WORKDIR}/udhcpc.script ${D}${sysconfdir}/
    install -m 755 ${WORKDIR}/dhcpswitch.sh ${D}${sysconfdir}/
    install -m 755 ${S}/source/scripts/init/service.d/logrotate.sh ${D}${sysconfdir}/cron/cron.every5minute/

    install -d ${D}${includedir}/ccsp
    install -m 644 ${S}/source/util/print_uptime/print_uptime.h ${D}${includedir}/ccsp

    # Creating symbolic links to install files in specific directory as in legacy builds
    ln -sf /usr/bin/syscfg ${D}${bindir}/syscfg_create
    ln -sf /usr/bin/syscfg ${D}${bindir}/syscfg_destroy
    ln -sf /usr/bin/syscfg ${D}${bindir}/syscfg_format
    ln -sf /usr/bin/syscfg ${D}${bindir}/syscfg_check
}


USERADD_PACKAGES = "${PN}"
GROUPADD_PARAM_${PN} = "--system firewall"
USERADD_PARAM_${PN} += "--system --home ${localstatedir}/run/firewall/ -M -g firewall --shell /bin/false firewall"

FILES_${PN} += "${sysconfdir}/utopia/"
FILES_${PN} += "${sysconfdir}/dhcp_static_hosts"
FILES_${PN} += "${sysconfdir}/IGD/"
FILES_${PN} += "${sysconfdir}/cron"
FILES_${PN} += "${sysconfdir}/utopia/service.d/"
FILES_${PN} += "${sysconfdir}/utopia/registrartion.d/"
FILES_${PN} += "${sysconfdir}/utopia/post.d/"
FILES_${PN} += "${sysconfdir}/utopia/service.d/service_bridge/"
FILES_${PN} += "${sysconfdir}/utopia/service.d/service_ddns/"
FILES_${PN} += "${sysconfdir}/utopia/service.d/service_dhcp_server/"
FILES_${PN} += "${sysconfdir}/utopia/service.d/service_lan/"
FILES_${PN} += "${sysconfdir}/utopia/service.d/service_multinet/"
FILES_${PN} += "${sysconfdir}/utopia/service.d/service_syslog/"
FILES_${PN} += "${sysconfdir}/utopia/service.d/service_wan/"
FILES_${PN} += "/fss/gw/etc/syslog.conf.${BPN}"

FILES_${PN}-dbg += " \
    ${prefix}/ccsp/.debug \
    ${prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

ALTERNATIVE_PRIORITY = "190"
ALTERNATIVE_${PN} = "syslog-conf"
ALTERNATIVE_LINK_NAME[syslog-conf] = "${sysconfdir}/syslog.conf"
ALTERNATIVE_TARGET[syslog-conf] = "/fss/gw/etc/syslog.conf.${BPN}"
CONFFILES_${PN} = "/fss/gw/${sysconfdir}/syslog.conf.${BPN}"

DEPENDS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'telemetry2_0', ' telemetry', '', d)}"
