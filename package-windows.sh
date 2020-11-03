#!/usr/bin/env bash

javapackager -deploy -native msi -outdir packages -outfile Breakout \
  -srcdir breakout/target -srcfiles breakout-0.0.0-SNAPSHOT.jar \
  -appclass org.breakout.Breakout -name Breakout -title "Breakout Cave Survey" \
  -BappVersion="${BREAKOUT_VERSION}" -Bicon=breakout.ico

