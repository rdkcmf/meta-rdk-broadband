SUMMARY = "RDK LED Manager component"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library rdk-logger utopia hal-platform hal-ledmanager libunpriv"

require ccsp_common.inc

SRC_URI ="${RDKB_CCSP_ROOT_GIT}/RdkLedManager/generic;protocol=${RDK_GIT_PROTOCOL};branch=${CCSP_GIT_BRANCH};name=LedManager"

SRCREV_LedManager = "${AUTOREV}"
SRCREV_FORMAT = "LedManager"

PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools pkgconfig

CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I ${STAGING_INCDIR}/syscfg \
    -I ${STAGING_INCDIR}/sysevent \
    "

LDFLAGS += " -lprivilege"

LDFLAGS_append_dunfell = " -ldbus-1"

do_install_append () {
    # Config files and scripts
    install -d ${D}${exec_prefix}/rdk/rdkledmanager
    ln -sf ${bindir}/rdkledmanager ${D}${exec_prefix}/rdk/rdkledmanager/rdkledmanager 
}


FILES_${PN} = " \
   ${exec_prefix}/rdk/rdkledmanager/rdkledmanager \
   ${bindir}/* \
"

FILES_${PN}-dbg = " \
    ${exec_prefix}/rdk/rdkledmanager/.debug \
    /usr/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"
