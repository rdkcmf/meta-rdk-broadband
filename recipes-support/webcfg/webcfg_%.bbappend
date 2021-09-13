DEPENDS += " utopia libunpriv "
CFLAGS_append = " \
    -I${STAGING_INCDIR}/syscfg \
    "
LDFLAGS +=" -lprivilege -lsyscfg"
FILESEXTRAPATHS_prepend := "${THISDIR}/files:"
SRC_URI_append += "file://Webcfg_drop_root.patch"

