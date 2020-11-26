SUMMARY = "CCSP Hotspot Kernel Module"
HOMEPAGE = "https://github.com/belvedere-yocto/hotspot"

LICENSE = "CLOSED"
#LIC_FILES_CHKSUM = "file://../../LICENSE;md5=7fd38647ff87fdac48b3fb87e20c1b07"

SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/cpc/mtu_modifier;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=hotspot-kmod"
CFLAGS += " -Wall -Werror -Wextra -Wno-pointer-sign -Wno-sign-compare "

SRCREV_hotspot-kmod = "${AUTOREV}"
SRCREV_FORMAT = "hotspot-kmod"
PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"

inherit module

PACKAGES += "kernel-module-${PN}"
