/**
 * Copyright (C) 2009-2010 Cars and Tracks Development Project (CTDP).
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 * 
 * @author Prunn
 * copyright@Prunn2011
 * 
 */
package com.prunn.rfdynhud.widgets.prunn._util;

import net.ctdp.rfdynhud.util.FontUtils;
import net.ctdp.rfdynhud.widgets.WidgetsConfiguration;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import net.ctdp.rfdynhud.widgets.base.widget.WidgetPackage;
import net.ctdp.rfdynhud.widgets.base.widget.WidgetSet;

public class PrunnWidgetSetv8 extends WidgetSet
{
    /*
     *  @author Prunn
     * copyright@Prunn2011
     */
    private PrunnWidgetSetv8()
    {
        super( composeVersion( 1, 0, 0 ) );
    }
    public static final PrunnWidgetSetv8 INSTANCE = new PrunnWidgetSetv8();
    
    public static final WidgetPackage WIDGET_PACKAGE = new WidgetPackage( INSTANCE, "Prunn", INSTANCE.getIcon( "com/prunn/rfdynhud/widgets/prunn/prunn.png" ) );
    public static final WidgetPackage WIDGET_PACKAGE_V8 = new WidgetPackage( INSTANCE, "Prunn/V8", INSTANCE.getIcon( "com/prunn/rfdynhud/widgets/prunn/prunn.png" ), INSTANCE.getIcon( "com/prunn/rfdynhud/widgets/prunn/v8.png" ) );
    
    public static final String FONT_COLOR1_NAME = "FontColor1";
    public static final String FONT_COLOR2_NAME = "FontColor2";
    public static final String FONT_COLOR3_NAME = "FontColor3";
    public static final String FONT_COLOR4_NAME = "FontColor4";
    public static final String GAP_FONT_COLOR1_NAME = "GapFontColor1";
    public static final String GAP_FONT_COLOR2_NAME = "GapFontColor2";
    public static final String V8_FONT_NAME = "v8Font";
    public static final String V8_FONT_NAME_TH = "v8thFont";
    public static final String V8_FONT_NAME_TH_BIG = "v8thFontBig";
    public static final String POS_FONT_NAME = "PosFont";
    public static final String INFO_NAME_FONT = "infoFont";
    
    public String getDefaultNamedColorValue( String name )
    {
        if(name.equals("StandardFontColor"))
            return "#E9E9E9";
        if ( name.equals( FONT_COLOR1_NAME ) )
            return ( "#14335E" );
        if ( name.equals( FONT_COLOR2_NAME ) )
            return ( "#EFEFEF" );
        if ( name.equals( FONT_COLOR3_NAME ) )
            return ( "#000000" );
        if ( name.equals( FONT_COLOR4_NAME ) )
            return ( "#A50000" );
        if ( name.equals( GAP_FONT_COLOR1_NAME ) )
            return ( "#FAFAFA" );
        if ( name.equals( GAP_FONT_COLOR2_NAME ) )
            return ( "#050505" );
        
        return ( null );
    }
    
    public String getDefaultNamedFontValue( String name )
    {
        if ( name.equals( V8_FONT_NAME ) )
            return ( FontUtils.getFontString( "Dialog", 1, 24, true, true ) );
        if ( name.equals( V8_FONT_NAME_TH ) )
            return ( FontUtils.getFontString( "Dialog", 1, 16, true, true ) );
        if ( name.equals( POS_FONT_NAME ) )
            return ( FontUtils.getFontString( "Dialog", 1, 66, true, true ) );
        if ( name.equals( V8_FONT_NAME_TH_BIG ) )
            return ( FontUtils.getFontString( "Dialog", 1, 22, true, true ) );
        if ( name.equals( INFO_NAME_FONT ) )
            return ( FontUtils.getFontString( "Dialog", 1, 36, true, true ) );
        
        return ( null );
    }
    
    @SuppressWarnings( "unchecked" )
    public static final <W extends Widget> W getWidgetByClass( Class<W> clazz, boolean includeSubclasses, WidgetsConfiguration widgetsConfig )
    {
        int n = widgetsConfig.getNumWidgets();
        
        if ( includeSubclasses )
        {
            for ( int i = 0; i < n; i++ )
            {
                Widget w = widgetsConfig.getWidget( i );
                
                if ( clazz.isAssignableFrom( w.getClass() ) )
                    return ( (W)w );
            }
        }
        else
        {
            for ( int i = 0; i < n; i++ )
            {
                Widget w = widgetsConfig.getWidget( i );
                
                if ( clazz == w.getClass() )
                    return ( (W)w );
            }
        }
        
        return ( null );
    }
}
