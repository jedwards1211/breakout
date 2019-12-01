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
			// // Black to White
			// new GradientModel(new float[]
			// { 0, 1 }, new Color[] { new Color(0, 0, 0), new Color(255, 255, 255)
			// }).reverse(),
			// // Blue Bright
			// new GradientModel(new float[]
			// { 0, 1 }, new Color[] { new Color(0, 0, 255), new Color(197, 200, 255)
			// }).reverse(),
			// // Blue Light to Dark
			// new GradientModel(new float[]
			// { 0, 1 }, new Color[] { new Color(34, 94, 140), new Color(214, 233, 242)
			// }).reverse(),
			// // Blue-Green Bright
			// new GradientModel(new float[]
			// { 0, 1 }, new Color[] { new Color(17, 207, 147), new Color(206, 248, 244)
			// }).reverse(),
			// // Blue-Green Light to Dark
			// new GradientModel(new float[]
			// { 0, 1 }, new Color[] { new Color(7, 72, 69), new Color(208, 240, 241)
			// }).reverse(),
			// // Brown Light to Dark
			// new GradientModel(new float[]
			// { 0, 1 }, new Color[] { new Color(95, 65, 42), new Color(241, 240, 183)
			// }).reverse(),
			// // Brown to Blue Green Diverging, Bright
			// new GradientModel(new float[]
			// { 0, 0.5, 1 },
			// new Color[]
			// { new Color(18, 127, 145), new Color(251, 255, 197), new Color(154, 83, 29)
			// }),
			// // Brown to Blue Green Diverging, Dark
			// new GradientModel(new float[]
			// { 0, 0.5, 1 }, new Color[] { new Color(39, 95, 99), new Color(202, 205,
			// 105), new Color(107, 67, 42) }),
			// // Coefficient Bias
			// new GradientModel(new float[]
			// { 0, 1 }, new Color[] { new Color(0, 50, 148), new Color(207, 212, 255)
			// }).reverse(),
			// // Cold to Hot Diverging
			// new GradientModel(new float[]
			// { 0, .5, 1 },
			// new Color[]
			// { new Color(215, 41, 32), new Color(251, 255, 197), new Color(197, 200, 255)
			// }),
			// // Condition Number
			// new GradientModel(new float[]
			// { 0, .5, 1 }, new Color[] { new Color(0, 96, 2), new Color(251, 255, 20),
			// new Color(253, 33, 5) })
			// .reverse(),
			// // Cyan to Purple
			// new GradientModel(new float[]
			// { 0, .5, 1 }, new Color[] { new Color(255, 0, 255), new Color(0, 0, 255),
			// new Color(0, 255, 255) })
			// .reverse(),
			// // Distance
			// new GradientModel(new float[]
			// { 0, 116 / 260, 190 / 260, 1 },
			// new Color[]
			// { new Color(252, 194, 15), new Color(253, 4, 63), new Color(253, 3, 147), new
			// Color(0, 0, 255) }),
			// // Elevation #1
			// new GradientModel(new float[]
			// { 0, 20 / 260, 42 / 260, 73 / 260, 80 / 260, 120 / 260, 157 / 260,
			// 197 / 250, 1 },
			// new Color[]
			// {
			// new Color(168, 241, 235),
			// new Color(170, 246, 187),
			// new Color(249, 253, 182),
			// new Color(0, 151, 51),
			// new Color(0, 128, 66),
			// new Color(248, 177, 13),
			// new Color(128, 0, 2),
			// new Color(106, 50, 16),
			// new Color(242, 243, 253) }).reverse(), };
			GradientModel
				.builder()
				.add(0, 255, 175, 153)
				.add(0.333, 151, 143, 82)
				.add(0.666, 2, 151, 134)
				.add(1, 70, 91, 118)
				.build(),
			GradientModel
				.builder()
				.add(0, 210, 255, 169)
				.add(0.3, 240, 193, 14)
				.add(0.55, 219, 128, 44)
				.add(1, 126, 59, 106)
				.build(),
			GradientModel
				.builder()
				.add(0, 210, 255, 169)
				.add(0.2, 240, 193, 14)
				.add(0.4, 219, 128, 44)
				.add(0.7, 126, 59, 106)
				.add(1, 6, 70, 79)
				.build(),
			GradientModel.builder().add(0, 250, 75, 65).add(1, 6, 73, 79).build(),
			GradientModel.builder().add(0, 250, 75, 65).add(0.5, 130, 105, 69).add(1, 6, 73, 79).build(),
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
			GradientModel.builder().add(0, 0, 0, 0).add(1.000, 255, 255, 255).build(),
			GradientModel.builder().add(0, 201, 200, 255).add(1.000, 11, 7, 224).build(),
			GradientModel.builder().add(0, 210, 229, 232).add(1.000, 48, 100, 140).build(),
			GradientModel.builder().add(0, 202, 245, 234).add(1.000, 50, 207, 146).build(),
			GradientModel.builder().add(0, 212, 240, 234).add(1.000, 22, 79, 74).build(),
			GradientModel.builder().add(0, 240, 236, 169).add(1.000, 102, 72, 48).build(),
			GradientModel.builder().add(0, 154, 83, 29).add(1.000, 39, 131, 145).build(),
			GradientModel.builder().add(0, 108, 67, 41).add(1.000, 51, 102, 102).build(),
			GradientModel.builder().add(0, 211, 212, 252).add(1.000, 1, 58, 148).build(),
			GradientModel.builder().add(0, 68, 116, 180).add(1.000, 217, 53, 41).build(),
			GradientModel.builder().add(0, 3, 96, 0).add(0.487, 252, 252, 0).add(1.000, 255, 42, 0).build(),
			GradientModel.builder().add(0, 5, 241, 245).add(0.502, 2, 0, 245).add(1.000, 241, 0, 245).build(),
			GradientModel.builder().add(0, 179, 236, 240).add(1.000, 10, 13, 148).build(),
			GradientModel.builder().add(0, 255, 194, 0).add(0.417, 255, 0, 64).add(1.000, 31, 0, 252).build(),
			GradientModel
				.builder()
				.add(0, 174, 240, 228)
				.add(0.140, 252, 252, 177)
				.add(0.277, 0, 128, 64)
				.add(0.421, 248, 184, 7)
				.add(0.554, 135, 8, 1)
				.add(0.712, 106, 50, 15)
				.add(1.000, 250, 247, 250)
				.build(),
			GradientModel
				.builder()
				.add(0, 118, 218, 203)
				.add(0.129, 248, 252, 195)
				.add(0.554, 141, 103, 43)
				.add(0.708, 150, 150, 181)
				.add(0.856, 182, 151, 182)
				.add(1.000, 250, 245, 250)
				.build(),
			GradientModel.builder().add(0, 252, 232, 211).add(1.000, 196, 12, 12).build(),
			GradientModel.builder().add(0, 218, 218, 218).add(1.000, 69, 69, 69).build(),
			GradientModel.builder().add(0, 203, 255, 203).add(1.000, 16, 204, 16).build(),
			GradientModel.builder().add(0, 219, 245, 233).add(1.000, 35, 102, 52).build(),
			GradientModel.builder().add(0, 29, 203, 16).add(0.506, 10, 239, 242).add(1.000, 11, 37, 227).build(),
			GradientModel.builder().add(0, 255, 234, 200).add(1.000, 240, 120, 7).build(),
			GradientModel.builder().add(0, 250, 232, 211).add(1.000, 171, 65, 36).build(),
			GradientModel
				.builder()
				.add(0, 242, 241, 159)
				.add(0.255, 255, 246, 0)
				.add(0.476, 255, 43, 0)
				.add(0.672, 240, 7, 238)
				.add(1.000, 20, 30, 176)
				.build(),
			GradientModel
				.builder()
				.add(0, 136, 36, 35)
				.add(0.251, 240, 151, 17)
				.add(0.502, 251, 250, 192)
				.add(1.000, 41, 34, 125)
				.build(),
			GradientModel.builder().add(0, 115, 76, 39).add(0.502, 251, 250, 192).add(1.000, 83, 15, 99).build(),
			GradientModel.builder().add(0, 156, 29, 112).add(1.000, 101, 112, 47).build(),
			GradientModel.builder().add(0, 94, 43, 69).add(1.000, 25, 61, 16).build(),
			GradientModel
				.builder()
				.add(0, 193, 79, 57)
				.add(0.395, 255, 254, 1)
				.add(0.590, 4, 219, 0)
				.add(1.000, 12, 49, 122)
				.build(),
			GradientModel.builder().add(0, 42, 147, 200).add(1.000, 232, 24, 21).build(),
			GradientModel.builder().add(0, 255, 203, 255).add(1.000, 199, 8, 199).build(),
			GradientModel.builder().add(0, 75, 29, 148).add(1.000, 26, 125, 15).build(),
			GradientModel.builder().add(0, 64, 12, 86).add(1.000, 27, 82, 16).build(),
			GradientModel.builder().add(0, 220, 180, 230).add(1.000, 113, 15, 242).build(),
			GradientModel.builder().add(0, 229, 212, 242).add(1.000, 93, 45, 112).build(),
			GradientModel.builder().add(0, 255, 203, 225).add(1.000, 199, 8, 100).build(),
			GradientModel.builder().add(0, 250, 214, 245).add(1.000, 143, 19, 58).build(),
			GradientModel.builder().add(0, 255, 201, 200).add(1.000, 219, 8, 4).build(),
			GradientModel.builder().add(0, 255, 221, 221).add(1.000, 143, 14, 11).build(),
			GradientModel.builder().add(0, 195, 66, 54).add(1.000, 54, 97, 207).build(),
			GradientModel.builder().add(0, 104, 10, 9).add(1.000, 19, 56, 97).build(),
			GradientModel.builder().add(0, 245, 7, 0).add(1.000, 20, 245, 0).build(),
			GradientModel.builder().add(0, 185, 21, 18).add(0.487, 255, 253, 189).add(1.000, 59, 148, 37).build(),
			GradientModel.builder().add(0, 94, 18, 9).add(0.506, 200, 201, 101).add(1.000, 19, 71, 17).build(),
			GradientModel.builder().add(0, 56, 167, 0).add(1.000, 255, 4, 0).build(),
			GradientModel
				.builder()
				.add(0, 255, 16, 0)
				.add(0.325, 255, 254, 0)
				.add(0.657, 11, 255, 254)
				.add(1.000, 15, 19, 255)
				.build(),
			GradientModel
				.builder()
				.add(0, 151, 8, 0)
				.add(0.321, 153, 150, 0)
				.add(0.657, 7, 153, 153)
				.add(1.000, 9, 12, 153)
				.build(),
			GradientModel.builder().add(0, 255, 153, 151).add(1.000, 153, 156, 255).build(),
			GradientModel.builder().add(0, 111, 151, 87).add(1.000, 252, 237, 251).build(),
			GradientModel
				.builder()
				.add(0, 255, 245, 255)
				.add(0.148, 241, 0, 255)
				.add(0.273, 33, 0, 255)
				.add(0.421, 9, 252, 255)
				.add(0.568, 24, 255, 1)
				.add(0.705, 252, 255, 0)
				.add(1.000, 133, 13, 0)
				.build(),
			GradientModel.builder().add(0, 252, 252, 252).add(1.000, 0, 0, 0).build(),
			GradientModel.builder().add(0, 255, 255, 126).add(1.000, 110, 8, 1).build(),
			GradientModel.builder().add(0, 251, 255, 123).add(0.336, 56, 224, 15).add(1.000, 15, 20, 122).build(),
			GradientModel.builder().add(0, 245, 241, 0).add(1.000, 245, 0, 0).build(),
			GradientModel.builder().add(0, 236, 252, 203).add(1.000, 158, 204, 18).build(),
			GradientModel.builder().add(0, 214, 240, 174).add(1.000, 96, 107, 45).build(), };
}
