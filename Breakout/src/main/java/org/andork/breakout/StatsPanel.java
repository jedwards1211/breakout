/*******************************************************************************
 * Breakout Cave Survey Visualizer
 *
 * Copyright (C) 2014 James Edwards
 *
 * jedwards8 at fastmail dot fm
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *******************************************************************************/
package org.andork.breakout;

import java.awt.Font;
import java.text.NumberFormat;
import java.util.Arrays;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.andork.awt.GridBagWizard;
import org.andork.awt.GridBagWizard.DefaultAutoInsets;
import org.andork.bind.BimapperBinder;
import org.andork.bind.Binder;
import org.andork.bind.QObjectAttributeBinder;
import org.andork.bind.ui.ComponentTextBinder;
import org.andork.breakout.StatsModel.MinAvgMax;
import org.andork.func.DoubleStringBimapper;
import org.andork.func.IntegerStringBimapper;
import org.andork.q.QObject;

public class StatsPanel extends JPanel
{
	Binder<QObject<StatsModel>>		modelBinder;
	Binder<QObject<MinAvgMax>>		distBinder;
	Binder<QObject<MinAvgMax>>		northBinder;
	Binder<QObject<MinAvgMax>>		eastBinder;
	Binder<QObject<MinAvgMax>>		depthBinder;
	QObjectAttributeBinder<Integer>	numSelectedBinder;
	QObjectAttributeBinder<Double>	totalDistanceBinder;
	NumberFormat					decimalFormat;

	JLabel							numSelectedCaptionLabel;
	JLabel							numSelectedLabel;
	JLabel							totalDistanceCaptionLabel;
	JLabel							totalDistanceLabel;
	MinAvgMaxLabels					distLabels;
	MinAvgMaxLabels					northLabels;
	MinAvgMaxLabels					eastLabels;
	MinAvgMaxLabels					depthLabels;

	public StatsPanel( Binder<QObject<StatsModel>> modelBinder )
	{
		this.modelBinder = modelBinder;
		init( );
	}

	public Binder<QObject<StatsModel>> getModelBinder( )
	{
		return modelBinder;
	}

	private void init( )
	{
		decimalFormat = NumberFormat.getInstance( );
		decimalFormat.setMinimumFractionDigits( 2 );
		decimalFormat.setMaximumFractionDigits( 2 );

		numSelectedBinder = new QObjectAttributeBinder<>( StatsModel.numSelected ).bind( modelBinder );
		totalDistanceBinder = new QObjectAttributeBinder<>( StatsModel.totalDistance ).bind( modelBinder );
		distBinder = new QObjectAttributeBinder<>( StatsModel.distStats ).bind( modelBinder );
		northBinder = new QObjectAttributeBinder<>( StatsModel.northStats ).bind( modelBinder );
		eastBinder = new QObjectAttributeBinder<>( StatsModel.eastStats ).bind( modelBinder );
		depthBinder = new QObjectAttributeBinder<>( StatsModel.depthStats ).bind( modelBinder );

		distLabels = new MinAvgMaxLabels( "Distance: " , distBinder );
		northLabels = new MinAvgMaxLabels( "North: " , northBinder );
		eastLabels = new MinAvgMaxLabels( "East: " , eastBinder );
		depthLabels = new MinAvgMaxLabels( "Depth: " , depthBinder );

		numSelectedCaptionLabel = new JLabel( "# Shots Selected: " );
		numSelectedCaptionLabel.setFont( numSelectedCaptionLabel.getFont( ).deriveFont( Font.BOLD ) );
		numSelectedLabel = new JLabel( );
		numSelectedLabel.setHorizontalAlignment( JLabel.RIGHT );
		totalDistanceCaptionLabel = new JLabel( "Total Distance: " );
		totalDistanceCaptionLabel.setFont( totalDistanceCaptionLabel.getFont( ).deriveFont( Font.BOLD ) );
		totalDistanceLabel = new JLabel( );
		totalDistanceLabel.setHorizontalAlignment( JLabel.RIGHT );

		new ComponentTextBinder( numSelectedLabel ).bind( new BimapperBinder<>( IntegerStringBimapper.instance ).bind(
			numSelectedBinder ) );
		new ComponentTextBinder( totalDistanceLabel ).bind(
			new BimapperBinder<>( new DoubleStringBimapper( decimalFormat ) ).bind( totalDistanceBinder ) );

		GridBagWizard gbw = GridBagWizard.create( this );

		gbw.defaults( ).autoinsets( new DefaultAutoInsets( 5 , 5 ) );
		gbw.defaults( ).east( );

		gbw.put( numSelectedCaptionLabel , numSelectedLabel ).x( 0 ).intoRow( ).y( 0 );
		gbw.put( totalDistanceCaptionLabel , totalDistanceLabel ).x( 0 ).intoRow( )
			.y( gbw.y( numSelectedCaptionLabel ) + 1 );
		gbw.put( numSelectedCaptionLabel , totalDistanceCaptionLabel ).west( ).fillx( 1.0 );

		JLabel minLabel = new JLabel( "Min" );
		minLabel.setFont( minLabel.getFont( ).deriveFont( Font.BOLD ) );
		JLabel avgLabel = new JLabel( "Avg" );
		avgLabel.setFont( avgLabel.getFont( ).deriveFont( Font.BOLD ) );
		JLabel maxLabel = new JLabel( "Max" );
		maxLabel.setFont( maxLabel.getFont( ).deriveFont( Font.BOLD ) );

		gbw.put( minLabel , avgLabel , maxLabel ).x( 1 ).intoRow( ).y( gbw.y( totalDistanceCaptionLabel ) + 1 );

		int y = 3;

		for( MinAvgMaxLabels labels : Arrays.asList( distLabels , northLabels , eastLabels , depthLabels ) )
		{
			gbw.put( labels.desc , labels.min , labels.avg , labels.max ).intoRow( ).y( y++ );
			gbw.put( labels.desc ).west( );
		}
	}

	private class MinAvgMaxLabels
	{
		Binder<QObject<MinAvgMax>>	modelBinder;

		JLabel						desc	= new JLabel( );
		JLabel						min		= new JLabel( );
		JLabel						avg		= new JLabel( );
		JLabel						max		= new JLabel( );

		public MinAvgMaxLabels( String desc , Binder<QObject<MinAvgMax>> modelBinder )
		{
			this.modelBinder = modelBinder;
			this.desc.setText( desc );
			this.desc.setFont( this.desc.getFont( ).deriveFont( Font.BOLD ) );
			min.setHorizontalAlignment( JLabel.RIGHT );
			avg.setHorizontalAlignment( JLabel.RIGHT );
			max.setHorizontalAlignment( JLabel.RIGHT );
			initBindings( );
		}

		private void initBindings( )
		{
			new ComponentTextBinder( min ).bind(
				new BimapperBinder<>( new DoubleStringBimapper( decimalFormat ) ).bind(
					new QObjectAttributeBinder<>( MinAvgMax.min ).bind( modelBinder ) ) );
			new ComponentTextBinder( avg ).bind(
				new BimapperBinder<>( new DoubleStringBimapper( decimalFormat ) ).bind(
					new QObjectAttributeBinder<>( MinAvgMax.avg ).bind( modelBinder ) ) );
			new ComponentTextBinder( max ).bind(
				new BimapperBinder<>( new DoubleStringBimapper( decimalFormat ) ).bind(
					new QObjectAttributeBinder<>( MinAvgMax.max ).bind( modelBinder ) ) );
		}
	}
}
