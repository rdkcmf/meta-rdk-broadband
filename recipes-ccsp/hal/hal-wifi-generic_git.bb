SUMMARY = "HAL for RDK CCSP components"
HOMEPAGE = "http://github.com/belvedere-yocto/hal"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://../../LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

PROVIDES = "hal-wifi"
RPROVIDES_${PN} = "hal-wifi"

DEPENDS += "halinterface"
SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/opensource/ccsp/hal;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=wifihal"

SRCREV_wifihal = "${AUTOREV}"
SRCREV_FORMAT = "wifihal"

PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git/source/wifi"

# Add flags to support mesh wifi if the feature is available.
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'meshwifi', '-DENABLE_FEATURE_MESHWIFI', '', d)}"
CFLAGS_append = " -I=${includedir}/ccsp "

inherit autotools coverity
