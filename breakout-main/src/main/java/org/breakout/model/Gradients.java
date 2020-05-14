package org.breakout.model;

import java.awt.Color;

public class Gradients {
	private Gradients() {

	}

	public static final GradientModel DEFAULT =
		new GradientModel(new float[]
		{ 0, 0.24f, 0.64f, 1 },
			new Color[]
			{ new Color(255, 249, 204), new Color(255, 195, 0), new Color(214, 6, 127), new Color(34, 19, 150) });

	public static final GradientModel[] CHOICES =
		{
			DEFAULT,
			// pink tan teal slate-blue
			GradientModel
				.builder()
				.add(0, 255, 175, 153)
				.add(0.333, 151, 143, 82)
				.add(0.666, 2, 151, 134)
				.add(1, 70, 91, 118)
				.build(),
			// mint gold pumpkin mauve
			GradientModel
				.builder()
				.add(0, 210, 255, 169)
				.add(0.3, 240, 193, 14)
				.add(0.55, 219, 128, 44)
				.add(1, 126, 59, 106)
				.build(),
			// mint gold pumpkin mauve turquoise
			GradientModel
				.builder()
				.add(0, 210, 255, 169)
				.add(0.2, 240, 193, 14)
				.add(0.4, 219, 128, 44)
				.add(0.7, 126, 59, 106)
				.add(1, 6, 70, 79)
				.build(),
			// watermelon tan turquoise
			GradientModel.builder().add(0, 250, 75, 65).add(0.5, 130, 105, 69).add(1, 6, 73, 79).build(),
			// sunset variants
			GradientModel.builder().add(0, 255, 105, 79).add(1, 1, 75, 107).build(),
			GradientModel.builder().add(0, 255, 221, 0).add(0.5, 255, 105, 79).add(1, 1, 75, 107).build(),
			GradientModel.builder().add(0, 255, 221, 169).add(0.5, 255, 105, 79).add(1, 1, 75, 107).build(),
			GradientModel
				.builder()
				.add(0, 255, 221, 169)
				.add(0.25, 255, 165, 46)
				.add(0.5, 255, 105, 79)
				.add(1, 1, 75, 107)
				.build(),
			// black white
			GradientModel.builder().add(0, 0, 0, 0).add(1.000, 255, 255, 255).build(),
			// green yellow red
			GradientModel.builder().add(0, 179, 236, 240).add(1.000, 10, 13, 148).build(),
			// toucan
			GradientModel.builder().add(0, 255, 194, 0).add(0.417, 255, 0, 64).add(1.000, 31, 0, 252).build(),
			// burnt-red light-yellow navy
			GradientModel
				.builder()
				.add(0, 136, 36, 35)
				.add(0.251, 240, 151, 17)
				.add(0.502, 251, 250, 192)
				.add(1.000, 41, 34, 125)
				.build(),
			// burnt-rainbow
			GradientModel
				.builder()
				.add(0, 193, 79, 57)
				.add(0.395, 255, 254, 1)
				.add(0.590, 4, 219, 0)
				.add(1.000, 12, 49, 122)
				.build(),
			// red steel-blue
			GradientModel.builder().add(0, 42, 147, 200).add(1.000, 232, 24, 21).build(),
			GradientModel.builder().add(0, 255, 203, 255).add(1.000, 199, 8, 199).build(),
			// lilac magenta
			GradientModel.builder().add(0, 255, 203, 225).add(1.000, 199, 8, 100).build(),
			// lilac dark-magenta
			GradientModel.builder().add(0, 250, 214, 245).add(1.000, 143, 19, 58).build(),
			// pink red
			GradientModel.builder().add(0, 255, 201, 200).add(1.000, 219, 8, 4).build(),
			// rose gradient
			GradientModel.builder().add(0, 255, 221, 221).add(1.000, 143, 14, 11).build(),
			// red light-yellow green
			GradientModel.builder().add(0, 185, 21, 18).add(0.487, 255, 253, 189).add(1.000, 59, 148, 37).build(),
			// rainbow
			GradientModel
				.builder()
				.add(0, 255, 16, 0)
				.add(0.325, 255, 254, 0)
				.add(0.657, 11, 255, 254)
				.add(1.000, 15, 19, 255)
				.build(),
			// dim rainbow
			GradientModel
				.builder()
				.add(0, 151, 8, 0)
				.add(0.321, 153, 150, 0)
				.add(0.657, 7, 153, 153)
				.add(1.000, 9, 12, 153)
				.build(),
			// grayscale
			GradientModel.builder().add(0, 252, 252, 252).add(1.000, 0, 0, 0).build(),
			// yellow brown
			GradientModel.builder().add(0, 255, 255, 126).add(1.000, 110, 8, 1).build(),
			// yellow green navy
			GradientModel.builder().add(0, 251, 255, 123).add(0.336, 56, 224, 15).add(1.000, 15, 20, 122).build(),
			// yellow red
			GradientModel.builder().add(0, 245, 241, 0).add(1.000, 245, 0, 0).build(),

			// MUTLI HUE
			// forest green
			GradientModel
				.palette(
					"#f7fcfd",
					"#e5f5f9",
					"#ccece6",
					"#99d8c9",
					"#66c2a4",
					"#41ae76",
					"#238b45",
					"#006d2c",
					"#00441b"),
			// vaporwave
			GradientModel
				.palette(
					"#f7fcfd",
					"#e0ecf4",
					"#bfd3e6",
					"#9ebcda",
					"#8c96c6",
					"#8c6bb1",
					"#88419d",
					"#810f7c",
					"#4d004b"),
			// seafoam
			GradientModel
				.palette(
					"#f7fcf0",
					"#e0f3db",
					"#ccebc5",
					"#a8ddb5",
					"#7bccc4",
					"#4eb3d3",
					"#2b8cbe",
					"#0868ac",
					"#084081"),
			// canyon
			GradientModel
				.palette(
					"#fff7ec",
					"#fee8c8",
					"#fdd49e",
					"#fdbb84",
					"#fc8d59",
					"#ef6548",
					"#d7301f",
					"#b30000",
					"#7f0000"),
			// cold blue
			GradientModel
				.palette(
					"#fff7fb",
					"#ece7f2",
					"#d0d1e6",
					"#a6bddb",
					"#74a9cf",
					"#3690c0",
					"#0570b0",
					"#045a8d",
					"#023858"),
			// lilac blue turquoise
			GradientModel
				.palette(
					"#fff7fb",
					"#ece2f0",
					"#d0d1e6",
					"#a6bddb",
					"#67a9cf",
					"#3690c0",
					"#02818a",
					"#016c59",
					"#014636"),
			// fabulous
			GradientModel
				.palette(
					"#f7f4f9",
					"#e7e1ef",
					"#d4b9da",
					"#c994c7",
					"#df65b0",
					"#e7298a",
					"#ce1256",
					"#980043",
					"#67001f"),
			// salmon purple
			GradientModel
				.palette(
					"#fff7f3",
					"#fde0dd",
					"#fcc5c0",
					"#fa9fb5",
					"#f768a1",
					"#dd3497",
					"#ae017e",
					"#7a0177",
					"#49006a"),
			// yellow green
			GradientModel
				.palette(
					"#ffffe5",
					"#f7fcb9",
					"#d9f0a3",
					"#addd8e",
					"#78c679",
					"#41ab5d",
					"#238443",
					"#006837",
					"#004529"),
			// yellow-green deep blue
			GradientModel
				.palette(
					"#ffffd9",
					"#edf8b1",
					"#c7e9b4",
					"#7fcdbb",
					"#41b6c4",
					"#1d91c0",
					"#225ea8",
					"#253494",
					"#081d58"),
			// burnt
			GradientModel
				.palette(
					"#ffffe5",
					"#fff7bc",
					"#fee391",
					"#fec44f",
					"#fe9929",
					"#ec7014",
					"#cc4c02",
					"#993404",
					"#662506"),
			// fire
			GradientModel
				.palette(
					"#ffffcc",
					"#ffeda0",
					"#fed976",
					"#feb24c",
					"#fd8d3c",
					"#fc4e2a",
					"#e31a1c",
					"#bd0026",
					"#800026"),

			// SINGLE HUE
			// blue
			GradientModel
				.palette(
					"#f7fbff",
					"#deebf7",
					"#c6dbef",
					"#9ecae1",
					"#6baed6",
					"#4292c6",
					"#2171b5",
					"#08519c",
					"#08306b"),
			// green
			GradientModel
				.palette(
					"#f7fcf5",
					"#e5f5e0",
					"#c7e9c0",
					"#a1d99b",
					"#74c476",
					"#41ab5d",
					"#238b45",
					"#006d2c",
					"#00441b"),
			// orange
			GradientModel
				.palette(
					"#fff5eb",
					"#fee6ce",
					"#fdd0a2",
					"#fdae6b",
					"#fd8d3c",
					"#f16913",
					"#d94801",
					"#a63603",
					"#7f2704"),
			// purple
			GradientModel
				.palette(
					"#fcfbfd",
					"#efedf5",
					"#dadaeb",
					"#bcbddc",
					"#9e9ac8",
					"#807dba",
					"#6a51a3",
					"#54278f",
					"#3f007d"),
			// off red
			GradientModel
				.palette(
					"#fff5f0",
					"#fee0d2",
					"#fcbba1",
					"#fc9272",
					"#fb6a4a",
					"#ef3b2c",
					"#cb181d",
					"#a50f15",
					"#67000d"),

			// DIVERGING
			// brown turquoise
			GradientModel
				.palette(
					"#8c510a",
					"#bf812d",
					"#dfc27d",
					"#f6e8c3",
					"#f5f5f5",
					"#c7eae5",
					"#80cdc1",
					"#35978f",
					"#01665e"),
			// magenta green
			GradientModel
				.palette(
					"#c51b7d",
					"#de77ae",
					"#f1b6da",
					"#fde0ef",
					"#f7f7f7",
					"#e6f5d0",
					"#b8e186",
					"#7fbc41",
					"#4d9221"),
			// purple green
			GradientModel
				.palette(
					"#762a83",
					"#9970ab",
					"#c2a5cf",
					"#e7d4e8",
					"#f7f7f7",
					"#d9f0d3",
					"#a6dba0",
					"#5aae61",
					"#1b7837"),
			// burnt-orange purple
			GradientModel
				.palette(
					"#b35806",
					"#e08214",
					"#fdb863",
					"#fee0b6",
					"#f7f7f7",
					"#d8daeb",
					"#b2abd2",
					"#8073ac",
					"#542788"),
			// hot cold
			GradientModel
				.palette(
					"#b2182b",
					"#d6604d",
					"#f4a582",
					"#fddbc7",
					"#f7f7f7",
					"#d1e5f0",
					"#92c5de",
					"#4393c3",
					"#2166ac"),
			// red black
			GradientModel
				.palette(
					"#b2182b",
					"#d6604d",
					"#f4a582",
					"#fddbc7",
					"#ffffff",
					"#e0e0e0",
					"#bababa",
					"#878787",
					"#4d4d4d"),
			// red yellow blue
			GradientModel
				.palette(
					"#d73027",
					"#f46d43",
					"#fdae61",
					"#fee090",
					"#ffffbf",
					"#e0f3f8",
					"#abd9e9",
					"#74add1",
					"#4575b4"),
			// red yellow green
			GradientModel
				.palette(
					"#d73027",
					"#f46d43",
					"#fdae61",
					"#fee08b",
					"#ffffbf",
					"#d9ef8b",
					"#a6d96a",
					"#66bd63",
					"#1a9850"),
			// rainbow
			GradientModel
				.palette(
					"#d53e4f",
					"#f46d43",
					"#fdae61",
					"#fee08b",
					"#ffffbf",
					"#e6f598",
					"#abdda4",
					"#66c2a5",
					"#3288bd"), };
}
