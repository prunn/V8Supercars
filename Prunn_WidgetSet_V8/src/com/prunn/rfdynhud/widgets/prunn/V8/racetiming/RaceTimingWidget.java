package com.prunn.rfdynhud.widgets.prunn.V8.racetiming;

import java.awt.Font;
import java.io.IOException;

import net.ctdp.rfdynhud.gamedata.FinishStatus;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.BooleanProperty;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.IntProperty;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.StandingsTools;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.values.StandingsView;
import net.ctdp.rfdynhud.values.StringValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetv8;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class RaceTimingWidget extends Widget
{
    private final ImagePropertyWithTexture imgFord= new ImagePropertyWithTexture( "imgDriver", "prunn/V8/Timing_Ford.png" );
    private final ImagePropertyWithTexture imgHolden = new ImagePropertyWithTexture( "imgDriverFirst", "prunn/V8/Timing_Holden.png" );
    
    protected final FontProperty f1_2011Font = new FontProperty("Main Font", PrunnWidgetSetv8.V8_FONT_NAME);
    private final ColorProperty fontColor2 = new ColorProperty( "fontColor2", PrunnWidgetSetv8.FONT_COLOR2_NAME );
    private final ColorProperty fontColor3 = new ColorProperty( "fontColor3", PrunnWidgetSetv8.FONT_COLOR3_NAME );
    private final FontProperty posFontTH = new FontProperty("v8thFont", PrunnWidgetSetv8.V8_FONT_NAME_TH);
    private DrawnString[] dsPos = null;
    private DrawnString[] dsPosTH = null;
    private DrawnString[] dsNumber = null;
    private DrawnString[] dsName = null;
    private DrawnString[] dsTime = null;
    private IntProperty fontyoffset = new IntProperty("Y Font Offset Row 1", 0);
    private IntProperty fontxposoffset = new IntProperty("X Position Font Offset", 0);
    private IntProperty fontxnameoffset = new IntProperty("X Name Font Offset", 0);
    private IntProperty fontxtimeoffset = new IntProperty("X Time Font Offset", 0);
    private final IntProperty numVeh = new IntProperty( "numberOfVehicles", 3 );
    private final IntValue currentSector = new IntValue();
    private final IntValue drawnCars = new IntValue();
    private VehicleScoringInfo[] vsis;
    private IntValue[] positions = null;
    private IntValue[] numbers = null;
    private StringValue[] names = null;
    private StringValue[] gaps = null;
    private Boolean init = false;
    private IntValue cveh = new IntValue();
    private IntValue cpos = new IntValue();
    private IntValue rtime = new IntValue();
    private IntValue sessiontime = new IntValue();
    private BooleanProperty relativeToCurrent = new BooleanProperty("Relative to viewed car", false);
    
    private BooleanProperty timeRefreshMode = new BooleanProperty("Time Mode",true);
    private IntProperty refreshTime = new IntProperty("Refresh Time", 10);
    
    
    
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onRealtimeEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
        drawnCars.reset();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initSubTextures( LiveGameData gameData, boolean isEditorMode, int widgetInnerWidth, int widgetInnerHeight, SubTextureCollector collector )
    {
    }
    
    @Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
        int maxNumItems = numVeh.getValue();
        dsPos = new DrawnString[maxNumItems];
        dsPosTH = new DrawnString[maxNumItems];
        dsNumber = new DrawnString[maxNumItems];
        dsName = new DrawnString[maxNumItems];
        dsTime = new DrawnString[maxNumItems];
        int fh = TextureImage2D.getStringHeight( "0%C", f1_2011Font );        
        
        imgFord.updateSize( width/maxNumItems, height, isEditorMode );
        imgHolden.updateSize( width/maxNumItems, height, isEditorMode );
        
        int top = height/2 - fh/2 + fontyoffset.getValue();
        int offx1 = width/maxNumItems*3/100;
        int offx1b = width/maxNumItems*13/200;
        int offx2 = width/maxNumItems*37/200;
        int offx3 = width/maxNumItems*25/100;
        int offx4 = width/maxNumItems*98/100;
        
        for(int i=0;i < maxNumItems;i++)
        { 
           dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", offx1 + i*width/maxNumItems + fontxposoffset.getValue(), top, Alignment.LEFT, false, getFont(), isFontAntiAliased(), fontColor3.getColor() );
           dsPosTH[i] = drawnStringFactory.newDrawnString( "dsPosTH", offx1b + i*width/maxNumItems + fontxposoffset.getValue(), top, Alignment.LEFT, false, posFontTH.getFont(), isFontAntiAliased(), fontColor3.getColor() );
           dsNumber[i] = drawnStringFactory.newDrawnString( "dsNumber", offx2 + i*width/maxNumItems + fontxposoffset.getValue(), top, Alignment.CENTER, false, getFont(), isFontAntiAliased(), fontColor2.getColor() );
           dsName[i] = drawnStringFactory.newDrawnString( "dsName", offx3 + i*width/maxNumItems + fontxnameoffset.getValue(), top, Alignment.LEFT, false, getFont(), isFontAntiAliased(), fontColor2.getColor() );
           dsTime[i] = drawnStringFactory.newDrawnString( "dsTime", offx4 + i*width/maxNumItems + fontxtimeoffset.getValue(), top, Alignment.RIGHT, false, getFont(), isFontAntiAliased(), fontColor2.getColor() );
            
        }
        
    }
    private static final String getTimeAsGapString2( float gap )
    {
        if ( gap == 0f )
            return ( TimingUtil.getTimeAsLaptimeString( 0f ) );
        
        if ( gap < 0f )
            return ( "- " + TimingUtil.getTimeAsLaptimeString( -gap ) );
        
        return ( "+ " + TimingUtil.getTimeAsLaptimeString( gap ) );
    }
    private void initArrays()
    {
        int maxNumItems = numVeh.getValue();
        positions = new IntValue[maxNumItems];
        numbers = new IntValue[maxNumItems];
        gaps = new StringValue[maxNumItems];
        names = new StringValue[maxNumItems];
        vsis = new VehicleScoringInfo[maxNumItems];
        
        for(int i=0;i < maxNumItems;i++)
        {
            positions[i] = new IntValue();
            positions[i].update( -1 );
            numbers[i] = new IntValue();
            numbers[i].update( -1 );
            gaps[i] = new StringValue();
            gaps[i].update( "" );
            names[i] = new StringValue();
            names[i].update( "" );
        }
        init = true; 
    }
    @Override
    protected Boolean updateVisibility( LiveGameData gameData, boolean isEditorMode )
    {
        super.updateVisibility( gameData, isEditorMode );
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        if(!init)
            initArrays();
        
        currentSector.update( scoringInfo.getLeadersVehicleScoringInfo().getSector() );
        cveh.update(scoringInfo.getViewedVehicleScoringInfo().getDriverId());
        rtime.update( (int)((scoringInfo.getSessionNanos() / 1000000000f) % refreshTime.getValue()) );
        sessiontime.update( (int)(scoringInfo.getSessionNanos() / 1000000000f) );
        
        if( currentSector.hasChanged() || cveh.hasChanged() || refreshTime.getValue() == 0 || ( timeRefreshMode.getValue() && rtime.hasChanged() && rtime.getValue() == 0 ) || (refreshTime.getValue() == 1 && sessiontime.hasChanged()))
        {
            StandingsTools.getDisplayedVSIsForScoring( scoringInfo, scoringInfo.getViewedVehicleScoringInfo(), false, StandingsView.RELATIVE_TO_LEADER, false, vsis );
            int maxNumItems = Math.min( numVeh.getValue(), scoringInfo.getNumVehicles());
            for(int i=0;i < maxNumItems;i++)
            {
                VehicleScoringInfo vsi = vsis[i];
                positions[i].update( vsi.getPlace( false ) );
                numbers[i].update( vsi.getVehicleInfo().getCarNumber() );
                names[i].update( ShortName(vsi.getDriverName()).toUpperCase() );
                if(vsi.getFinishStatus() == FinishStatus.DNF || vsi.getFinishStatus() == FinishStatus.DQ)
                    gaps[i].update( "OUT" );
                else
                    if(vsi.isInPits())
                        gaps[i].update( "PIT" );
                    else
                        if(relativeToCurrent.getValue() && scoringInfo.getViewedVehicleScoringInfo().getPlace( false ) > 1 && scoringInfo.getViewedVehicleScoringInfo().getLapsBehindLeader( false ) == 0)
                            gaps[i].update(getTimeAsGapString2( -(scoringInfo.getViewedVehicleScoringInfo().getTimeBehindLeader( false ) - vsi.getTimeBehindLeader( false ))) );
                        else
                            if( vsi.getPlace( false ) == 1 )
                                gaps[i].update( "" );
                            else
                                if(vsi.getLapsBehindLeader( false ) > 0)
                                {
                                    String laps = ( vsi.getLapsBehindLeader( false ) > 1 ) ? " Laps" : " Lap";
                                    gaps[i].update( "+ " + String.valueOf( vsi.getLapsBehindLeader( false ) ) + laps);
                                }   
                                else
                                    gaps[i].update( "+ " + TimingUtil.getTimeAsLaptimeString( Math.abs(vsi.getTimeBehindLeader( false )) ));
                                
                
            }
            
            cpos.update(scoringInfo.getViewedVehicleScoringInfo().getPlace( false ));
            if(cpos.hasChanged() && !isEditorMode)
                forceCompleteRedraw( true );
        }
        
        return true;
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        int maxNumItems = numVeh.getValue();
        
        for(int i=0;i < maxNumItems;i++)
        {    
            if(positions[i].getValue() != -1 && gameData.getScoringInfo().getVehicleScoringInfo( positions[i].getValue() -1 ).getVehicleInfo().getManufacturer().toUpperCase().equals( "FORD" ))
                texture.clear( imgFord.getTexture(), offsetX + i*width/maxNumItems, offsetY, false, null );
            else
                if(positions[i].getValue() != -1)
                    texture.clear( imgHolden.getTexture(), offsetX + i*width/maxNumItems, offsetY, false, null );
        }
        
    }
    
    private String ShortName( String driverName )
    {
        int sp = driverName.lastIndexOf( ' ' );
        if ( sp == -1 )
        {
            return ( driverName );
        }
        
        String sf = driverName.substring( sp + 1 );
        
        return ( sf );
    }
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int maxNumItems = Math.min( numVeh.getValue(), scoringInfo.getNumVehicles());
        
        for(int i=0;i < maxNumItems;i++)
        { 
            if( needsCompleteRedraw || ( clock.c() && positions[i].hasChanged() ) )
            {
                if(positions[i].getValue() != -1 && gaps[i].getValue() != "OUT" )
                {
                    if(positions[i].getValue() == 1)
                        dsPosTH[i].draw( offsetX, offsetY, "ST", texture );
                    else if(positions[i].getValue() == 2)
                        dsPosTH[i].draw( offsetX, offsetY, "ND", texture );
                    else if(positions[i].getValue() == 3)
                        dsPosTH[i].draw( offsetX, offsetY, "RD", texture );
                    else if(positions[i].getValue() >= 10)
                        dsPosTH[i].draw( offsetX, offsetY, "  TH", texture );
                    else
                        dsPosTH[i].draw( offsetX, offsetY, "TH", texture );
                    
                    dsPos[i].draw( offsetX, offsetY, positions[i].getValueAsString(), texture );
                    
                }
                else
                    dsPos[i].draw( offsetX, offsetY, "", texture );
                dsNumber[i].draw( offsetX, offsetY, numbers[i].getValueAsString(), texture );
            } 
            if( needsCompleteRedraw || ( clock.c() && names[i].hasChanged() ) )
                dsName[i].draw( offsetX, offsetY, names[i].getValue(), texture );
            if( needsCompleteRedraw || ( clock.c() && gaps[i].hasChanged() ) )
            {
                if(gaps[i].getValue() == "PIT")
                    dsTime[i].draw( offsetX, offsetY, gaps[i].getValue(), fontColor3.getColor(), texture );
                else
                    dsTime[i].draw( offsetX, offsetY, gaps[i].getValue(), fontColor2.getColor(),texture );
            }
        }
       
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );

        writer.writeProperty( f1_2011Font, "" );
        writer.writeProperty( posFontTH, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( fontColor3, "" );
        writer.writeProperty( numVeh, "" );
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( fontxposoffset, "" );
        writer.writeProperty( fontxnameoffset, "" );
        writer.writeProperty( fontxtimeoffset, "" );
        writer.writeProperty( timeRefreshMode, "timeRefreshMode" );
        writer.writeProperty( refreshTime, "refreshTime" );
        writer.writeProperty( relativeToCurrent, "relativeToCurrent" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( f1_2011Font ) );
        else if ( loader.loadProperty( posFontTH ) );
        else if ( loader.loadProperty( fontColor3 ) );
        else if ( loader.loadProperty( numVeh ) );
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( fontxposoffset ) );
        else if ( loader.loadProperty( fontxnameoffset ) );
        else if ( loader.loadProperty( fontxtimeoffset ) );
        else if ( loader.loadProperty( timeRefreshMode ) );
        else if ( loader.loadProperty( refreshTime ) );
        else if ( loader.loadProperty( relativeToCurrent ) );
    }
    
    @Override
    protected void addFontPropertiesToContainer( PropertiesContainer propsCont, boolean forceAll )
    {
        propsCont.addGroup( "Colors and Fonts" );
        
        super.addFontPropertiesToContainer( propsCont, forceAll );
        propsCont.addProperty( f1_2011Font );
        propsCont.addProperty( posFontTH );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty( fontColor3 );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
        propsCont.addProperty( timeRefreshMode );
        propsCont.addProperty( refreshTime );
        propsCont.addProperty( numVeh );
        propsCont.addGroup( "Font Displacement" );
        propsCont.addProperty( fontyoffset );
        propsCont.addProperty( fontxposoffset );
        propsCont.addProperty( fontxnameoffset );
        propsCont.addProperty( fontxtimeoffset );
        propsCont.addProperty( relativeToCurrent );
    }
    
    @Override
    protected boolean canHaveBorder()
    {
        return ( false );
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public void prepareForMenuItem()
    {
        super.prepareForMenuItem();
        
        getFontProperty().setFont( "Dialog", Font.PLAIN, 6, false, true );
    }
    
    public RaceTimingWidget()
    {
        super( PrunnWidgetSetv8.INSTANCE, PrunnWidgetSetv8.WIDGET_PACKAGE_V8, 75.0f, 8.9f );
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetv8.V8_FONT_NAME );
        getFontColorProperty().setColor( PrunnWidgetSetv8.FONT_COLOR1_NAME );
    }
}
