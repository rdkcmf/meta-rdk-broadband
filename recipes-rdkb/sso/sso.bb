LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=3b83ef96387f14655fc854ddc3c6bd57"

inherit autotools pkgconfig coverity
DEPENDS = "curl"

SRC_URI = "${CMF_GIT_ROOT}/rdkb/components/generic/sso;protocol=${CMF_GIT_PROTOCOL};branch=${CMF_GIT_BRANCH};name=sso"
DEPENDS_append_dunfell = " openssl"

PV = "${RDK_RELEASE}+git${SRCPV}"

SRCREV_sso= "${AUTOREV}"
SRCREV_FORMAT = "sso"

S = "${WORKDIR}/git"

do_install_append() {

        # make sso_api.h available to include from other projects
        install -d ${D}${includedir}
        install -m 0644 ${S}/include/sso_api.h ${D}${includedir}
}
