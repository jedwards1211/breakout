#!/usr/bin/env bash

javapackager -deploy -native -outdir packages -outfile Breakout \
  -srcdir breakout/target -srcfiles breakout-$(./getVersion.sh).jar \
  -appclass org.breakout.Breakout -name Breakout -title "Breakout Cave Survey" \
  -BappVersion=$(./getVersion.sh)

