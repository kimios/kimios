#!/bin/sh
echo "Building bundle from repository binaries for $TOMCAT_DIR"
rm -Rf "$TARGET_DIR/$BUNDLE_DIR"
mkdir -p "$TARGET_DIR/"
echo "Destination $TARGET_DIR/$BUNDLE_DIR"
curl -O $TOMCAT_ARCHIVE
unzip -d "$TARGET_DIR/" $TOMCAT_NAME.zip
TOMCAT_DIR="$TARGET_DIR/$TOMCAT_NAME"
#tomcat copy

mv $TARGET_DIR/$TOMCAT_NAME $TARGET_DIR/$BUNDLE_DIR

ls ./scripts/kimios.*
cp ./scripts/kimios.* $TARGET_DIR/$BUNDLE_DIR/
cp ./scripts/setenv.* $TARGET_DIR/$BUNDLE_DIR/bin/
cp kimios_howto_install_bundle.txt $TARGET_DIR/$BUNDLE_DIR

rm -Rf $TARGET_DIR/$BUNDLE_DIR/webapps/*

chmod +x $TARGET_DIR/$BUNDLE_DIR/bin/*.sh
chmod +x $TARGET_DIR/$BUNDLE_DIR/*.sh

echo "Ending Bundle Build"
