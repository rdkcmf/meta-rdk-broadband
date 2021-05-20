SUMMARY = "jst for webui which includes duktape and ccsp sources."
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=e76996dff7c96f34b60249db92fc7aeb"

DEPENDS = "ccsp-common-library ${@bb.utils.contains('DISTRO_FEATURES', 'rbus', '', 'dbus', d)}"

SRC_URI = "${RDK_GENERIC_ROOT_GIT}/jst/generic;protocol=${RDK_GIT_PROTOCOL};branch=${RDK_GIT_BRANCH}"

PV = "${RDK_RELEASE}+git${SRCPV}"
SRCREV ?= "${AUTOREV}"
S = "${WORKDIR}/git"
  
inherit cmake

EXTRA_OECMAKE += "-DBUILD_RDK=ON "

CFLAGS_append = " \
    ${@bb.utils.contains('DISTRO_FEATURES', 'rbus', '-DBUILD_RBUS', '-I=${includedir}/dbus-1.0 -I=${libdir}/dbus-1.0/include', d)} \
    -I=${includedir}/ccsp \
    "
LDFLAGS_append += "${@bb.utils.contains('DISTRO_FEATURES', 'rbus', '', '-ldbus-1', d)}"

DEPENDS += " ${@bb.utils.contains('DISTRO_FEATURES', 'comcast_sso_remove', '', 'sso', d)}"
CFLAGS_append = " ${@bb.utils.contains('DISTRO_FEATURES', 'comcast_sso_remove', '', '-DCOMCAST_SSO', d)}"
LDFLAGS_append += " ${@bb.utils.contains('DISTRO_FEATURES', 'comcast_sso_remove', '', '-lsso', d)}"

do_install_append() {
 install -d ${D}/usr/www2/
 install -d ${D}/usr/www2/includes
 install -d ${D}/usr/video_analytics
 install -m 755 ${S}/jsts/php.jst ${D}/usr/www2/includes
 install -m 755 ${S}/jsts/jst_prefix.js ${D}/usr/www2/
 install -m 755 ${S}/jsts/jst_suffix.js ${D}/usr/www2/
 install -m 755 ${S}/jsts/php.jst ${D}/usr/video_analytics/
 install -m 755 ${S}/jsts/jst_prefix.js ${D}/usr/video_analytics/
 install -m 755 ${S}/jsts/jst_suffix.js ${D}/usr/video_analytics/
}
FILES_${PN}-ccsp = " \
"
FILES_${PN} += "/usr/*"
FILES_${PN} += "/usr/www2/*"
FILES_${PN} += "/usr/www2/includes/*"
FILES_${PN} += "/usr/video_analytics/*"


