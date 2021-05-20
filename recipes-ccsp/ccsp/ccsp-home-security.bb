SUMMARY = "CCSP Home Security."
HOMEPAGE = "http://github.com/belvedere-yocto/CcspHomeSecurity"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "libxml2 ccsp-common-library utopia curl"
require ccsp_common.inc
SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/CcspHomeSecurity;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=CcspHomeSecurity"

SRCREV_CcspHomeSecurity = "${AUTOREV}"
SRCREV_FORMAT = "CcspHomeSecurity"
PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit autotools

CFLAGS += " -Wall -Werror -Wextra "

CFLAGS_append_dunfell = " -Wno-format-truncation -Wno-format-overflow "

CFLAGS_append = " \
    -I=${includedir}/dbus-1.0 \
    -I=${libdir}/dbus-1.0/include \
    -I=${includedir}/ccsp \
    -I=${includedir}/libxml2 \
    "

do_install_append () {
}

PACKAGES += "${PN}-ccsp"

FILES_${PN}-ccsp = " \
"

FILES_${PN}-dbg = " \
    ${prefix}/ccsp/.debug \
    ${prefix}/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"

