SUMMARY = "HAL for RDK CCSP components"
HOMEPAGE = "http://github.com/belvedere-yocto/hal"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../../LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

PROVIDES = "hal-moca"
RPROVIDES_${PN} = "hal-moca"

DEPENDS += "halinterface"
SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/hal;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=mocahal"

SRCREV_mocahal = "${AUTOREV}"
SRCREV_FORMAT = "mocahal"

PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git/source/moca"

CFLAGS_append = " -I=${includedir}/ccsp "

inherit autotools coverity
