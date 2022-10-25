DESCRIPTION = "Ad-Blocker"
LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=86d3f3a95c324c9479bd8986968f4327"

SRCREV = "${AUTOREV}"

DEPENDS = "libnfnetlink libnetfilter-queue"

LDFLAGS = "-lnetfilter_queue -lnfnetlink -lresolv"

SRC_URI = "git://github.com/bunnam988/ad-blocker;branch=main"
S = "${WORKDIR}/git"

do_compile() {
${CC} ${CFLAGS} ${LDFLAGS} ${S}/ad-blocker.c -o ad-blocker
}

do_install() {
install -d ${D}${bindir}
install -m 0755 ad-blocker ${D}${bindir}
}
