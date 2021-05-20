CXXFLAGS_append += " -D_RDK_BROADBAND_PRIV_CAPS_ "
require recipes-ccsp/ccsp/ccsp_common.inc

do_install_append(){
        install -d ${D}${sysconfdir}
        install -d ${D}${sysconfdir}/security/caps/
        install -m 755 ${S}/source/process-capabilities_rdkb.json ${D}${sysconfdir}/security/caps/process-capabilities.json
}

FILES_${PN} += " ${sysconfdir}/security/caps/process-capabilities.json"
