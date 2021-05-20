##########################################################################
# If not stated otherwise in this file or this component's Licenses.txt
# file the following copyright and licenses apply:
#
# Copyright 2019 RDK Management
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
# http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
##########################################################################

SUMMARY = "RDK GPON Manager component"

LICENSE = "Apache-2.0"
LIC_FILES_CHKSUM = "file://LICENSE;md5=175792518e4ac015ab6696d16c4f607e"

DEPENDS = "ccsp-common-library dbus rdk-logger utopia hal-platform json-hal-lib"

require ccsp_common.inc

SRC_URI ="${RDKB_CCSP_ROOT_GIT}/RdkGponManager/generic;protocol=${RDK_GIT_PROTOCOL};branch=${CCSP_GIT_BRANCH};name=GponManager"

SRCREV_GponManager = "${AUTOREV}"
SRCREV_FORMAT = ""

PV = "${RDK_RELEASE}+git${SRCPV}"

S = "${WORKDIR}/git"
B = "${WORKDIR}/build"

inherit autotools pkgconfig

CFLAGS_append = " \
    -I${STAGING_INCDIR} \
    -I${STAGING_INCDIR}/dbus-1.0 \
    -I${STAGING_LIBDIR}/dbus-1.0/include \
    -I${STAGING_INCDIR}/ccsp \
    -I ${STAGING_INCDIR}/syscfg \
    -I ${STAGING_INCDIR}/sysevent \
    "
CFLAGS_append  = " ${@bb.utils.contains('DISTRO_FEATURES', 'rdkb_wan_manager', '-DFEATURE_RDKB_WAN_MANAGER', '', d)}"

do_install () {
    # Config files and scripts
    install -d ${D}/usr/rdk
    install -d ${D}/usr/rdk/gponmanager
    install -d ${D}${bindir}
    install -d ${D}${sysconfdir}/rdk/conf
    install -d ${D}${sysconfdir}/rdk/schemas
    install -m 755 ${B}/source/GponManager/GponManager ${D}${bindir}
    install -m 644 ${S}/config/RdkGponManager.xml ${D}/usr/rdk/gponmanager
    install -m 644 ${S}/config/gpon_manager_conf.json ${D}${sysconfdir}/rdk/conf
    install -m 644 ${S}/hal_schema/gpon_hal_schema.json ${D}${sysconfdir}/rdk/schemas
}

FILES_${PN} = " \
   ${bindir}/GponManager \
   ${prefix}/rdk/gponmanager/RdkGponManager.xml \
   ${sysconfdir}/rdk/conf \
   ${sysconfdir}/rdk/conf/gpon_manager_conf.json \
   ${sysconfdir}/rdk/schemas \
   ${sysconfdir}/rdk/schemas/gpon_hal_schema.json \
"

FILES_${PN}-dbg = " \
    ${prefix}/rdk/gponmanager/.debug \
    /usr/src/debug \
    ${bindir}/.debug \
    ${libdir}/.debug \
"
INSANE_SKIP_${PN} += "dev-deps"
