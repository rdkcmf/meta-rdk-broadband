#
# ============================================================================
# COMCAST C O N F I D E N T I A L AND PROPRIETARY
# ============================================================================
# This file and its contents are the intellectual property of Comcast.  It may
# not be used, copied, distributed or otherwise  disclosed in whole or in part
# without the express written permission of Comcast.
# ============================================================================
# Copyright (c) 2018 Comcast. All rights reserved.
# ============================================================================
#
SUMMARY = "This receipe provides dhcp manager component support."
SECTION = "console/utils"
#LICENSE = "Apache-2.0"
LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library dbus utopia ccsp-lm-lite"
DEPENDS_append = " hal-cm hal-dhcpv4c hal-ethsw hal-moca hal-mso_mgmt hal-mta hal-platform hal-vlan hal-wifi curl ccsp-misc ccsp-hotspot cjson libsyswrapper halinterface libunpriv "
require ccsp_common.inc
SRC_URI = "${RDKB_CCSP_ROOT_GIT}/DhcpManager/generic;protocol=${RDK_GIT_PROTOCOL};branch=${CCSP_GIT_BRANCH};name=DhcpManager"
CFLAGS += " -Wall -Werror -Wextra -Wno-shift-negative-value"
CFLAGS_append_dunfell = " -Wno-format-truncation -Wno-incompatible-pointer-types -Wno-format-overflow -Wno-deprecated-declarations -Wno-sizeof-pointer-memaccess -Wno-memset-elt-size -Wno-maybe-uninitialized "

S = "${WORKDIR}/git"

PV = "${RDK_RELEASE}+git${SRCPV}"
SRCREV = "${AUTOREV}"
SRCREV_FORMAT = "${AUTOREV}"




inherit autotools
inherit autotools pythonnative


#PACKAGECONFIG ?= "dropearly"
#PACKAGECONFIG[dropearly] = "--enable-dropearly,--disable-dropearly"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec',  ' `pkg-config --cflags libsafec`', '-fPIC', d)}"
CFLAGS_append  = " ${@bb.utils.contains('DISTRO_FEATURES', 'dhcp_manager', '-DFEATURE_RDKB_DHCP_MANAGER', '', d)}"

LDFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', ' `pkg-config --libs libsafec`', '', d)}"
LDFLAGS_remove_dunfell = "${@bb.utils.contains('DISTRO_FEATURES', 'safec', '-lsafec-3.5', '', d)}"
LDFLAGS_append = "${@bb.utils.contains('DISTRO_FEATURES', 'safec dunfell', ' -lsafec-3.5.1 ', '', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'safec', '', ' -DSAFEC_DUMMY_API', d)}"
#CFLAGS_append  = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', ' -DFEATURE_RDKB_WAN_MANAGER', '', d)}"
#LDFLAGS_append_dunfell = " -lrt"

CFLAGS_append = " -UFEATURE_RDKB_WAN_MANAGER"
CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I${STAGING_INCDIR}/utapi \
    -I${STAGING_INCDIR}/wrp-c \
    -I${STAGING_INCDIR}/utctx \
    -I${STAGING_INCDIR}/ulog \
    -I${STAGING_INCDIR}/syscfg \
    -I${STAGING_INCDIR}/cjson \
    "
LDFLAGS_append = " \
    -lccsp_common \
    -ldbus-1 \
    -lutctx \
    -lutapi \
    -lulog \
    -lcjson \
    -lm \
    -lwrp-c \
    -lapi_dhcpv4c \
    -lsysevent \
    -lsecure_wrapper \
    -lprivilege \
    "
do_compile_prepend () {
	(python ${STAGING_BINDIR_NATIVE}/dm_pack_code_gen.py ${S}/config/TR181-DHCPMgr.XML ${S}/source/DHCPMgrSsp/dm_pack_datamodel.c)
}

do_install_append () {
    # Config files and scripts
    install -d ${D}/usr/ccsp/dhcpmgr
    install -m 644 ${S}/config/TR181-DHCPMgr.XML -t ${D}/usr/ccsp/dhcpmgr
}
FILES_${PN} += " \
    ${prefix}/ccsp/dhcpmgr/TR181-DHCPMgr.XML  \
    ${bindir}/* \
"
