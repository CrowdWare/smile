#!/bin/bash

PLUGIN_NAME="sml-render"
ZIP_NAME="${PLUGIN_NAME}.zip"

# Zielpfad bereinigen
rm -f $ZIP_NAME

# Plugin verpacken
zip -r $ZIP_NAME $PLUGIN_NAME.php -x "*.zip" "build.sh"

echo "✅ Plugin verpackt: $ZIP_NAME"