SUMMARY = "RDK EasyMesh Controller component"

LICENSE = "BSD-2-Clause-Patent & Apache-2.0"
LIC_FILES_CHKSUM = " \
    file://LICENSE;md5=ad1d394d8d45e5585e02e2bc6eb139e4 \
    file://ssp/LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57 \
"

DEPENDS = "ccsp-common-library dbus rdk-logger json-c libubox openssl zlib"

require ccsp_common.inc

SRC_URI =  "${CMF_GITHUB_ROOT}/RdkEasyMeshController;protocol=${CMF_GIT_PROTOCOL};branch=main;name=RdkEasyMeshController"
SRC_URI += "${CMF_GITHUB_ROOT}/RdkEasyMeshControllerSSP;protocol=${CMF_GIT_PROTOCOL};branch=main;destsuffix=git/ssp;name=ssp"

SRCREV_RdkEasyMeshController = "${AUTOREV}"
SRCREV_ssp = "${AUTOREV}"
SRCREV_FORMAT = "RdkEasyMeshController"

PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

# inherit cmake pkgconfig
inherit autotools pkgconfig

CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I${STAGING_INCDIR}/libubox \
"

LDFLAGS_append = " \
    -ldbus-1 \
    -lrdkloggers \
    -ljson-c \
    -lz \
    -lcrypto \
    -lpthread \
    -L${STAGING_LIBDIR} -lubox \
"

do_install_append () {
    # Config files and scripts
    install -d ${D}${exec_prefix}/ccsp/easymesh
    ln -sf ${bindir}/em-ctl ${D}${exec_prefix}/ccsp/easymesh/em-ctl
    install -m 644 ${S}/ssp/scripts/RdkEasyMeshController.xml ${D}${exec_prefix}/ccsp/easymesh/RdkEasyMeshController.xml
    #if [ ${ISSYSTEMD} = "true" ]; then
        #install -d ${D}${systemd_unitdir}/system
        #install -D -m 0644 ${S}/scripts/RdkEasyMeshController.service ${D}${systemd_unitdir}/system/RdkEasyMeshController.service
    #fi
}

#SYSTEMD_SERVICE_${PN} += " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', 'RdkEasyMeshController.service', '', d)}"

FILES_${PN} += " \
    ${exec_prefix}/ccsp/easymesh/em-ctl \
    ${exec_prefix}/ccsp/easymesh/RdkEasyMeshController.xml \
    ${bindir}/* \
"

#FILES_${PN}_append += " ${@bb.utils.contains('DISTRO_FEATURES', 'systemd', '${systemd_unitdir}/system/RdkEasyMeshController.service', '', d)}"
