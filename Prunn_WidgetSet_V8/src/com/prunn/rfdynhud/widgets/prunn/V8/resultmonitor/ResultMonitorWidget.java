package com.prunn.rfdynhud.widgets.prunn.V8.resultmonitor;

import java.awt.Color;
import java.awt.Font;
import java.io.IOException;

import net.ctdp.rfdynhud.gamedata.FinishStatus;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.SessionType;
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
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.FloatValue;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.values.StringValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetv8;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class ResultMonitorWidget extends Widget
{
    private DrawnString[] dsPos = null;
    private DrawnString[] dsPosTH = null;
    private DrawnString[] dsCarNumber = null;
    private DrawnString[] dsName = null;
    private DrawnString[] dsTime = null;
    private DrawnString dsTrack = null;
    
    private final ImagePropertyWithTexture imgTrack = new ImagePropertyWithTexture( "imgTrack", "prunn/V8/Results_title.png" );
    private final ImagePropertyWithTexture imgFord = new ImagePropertyWithTexture( "imgFirst", "prunn/V8/Results_Ford.png" );
    private final ImagePropertyWithTexture imgHolden = new ImagePropertyWithTexture( "imgPos", "prunn/V8/Results_Holden.png" );
    
    protected final FontProperty f1_2011Font = new FontProperty("Main Font", PrunnWidgetSetv8.V8_FONT_NAME);
    private final FontProperty posFontTH = new FontProperty("v8thFont", PrunnWidgetSetv8.V8_FONT_NAME_TH);
    private final ColorProperty fontColor3 = new ColorProperty( "fontColor3", PrunnWidgetSetv8.FONT_COLOR3_NAME );
    private final ColorProperty fontColor2 = new ColorProperty( "fontColor2", PrunnWidgetSetv8.FONT_COLOR2_NAME );
    
    private final IntProperty numVeh = new IntProperty( "numberOfVehicles", 10 );
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    private IntProperty fontxposoffset = new IntProperty("X Position Font Offset", 0);
    private IntProperty fontxnameoffset = new IntProperty("X Name Font Offset", 0);
    private IntProperty fontxtimeoffset = new IntProperty("X Time Font Offset", 0);
    
    private IntValue[] positions = null;
    private IntValue[] carNumbers = null;
    private StringValue[] driverNames = null;
    private FloatValue[] gaps = null;
    private BooleanProperty AbsTimes = new BooleanProperty("Use absolute times", false) ;
    
    
    @Override
    public void onRealtimeEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onCockpitEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected void initSubTextures( LiveGameData gameData, boolean isEditorMode, int widgetInnerWidth, int widgetInnerHeight, SubTextureCollector collector )
    {
    }
    
    private void initValues()
    {
        int maxNumItems = numVeh.getValue();
        
        if ( ( positions != null ) && ( positions.length == maxNumItems ) )
            return;
        
        gaps = new FloatValue[maxNumItems];
        positions = new IntValue[maxNumItems];
        carNumbers = new IntValue[maxNumItems];
        driverNames = new StringValue[maxNumItems];
        
        for(int i=0;i < maxNumItems;i++)
        { 
            positions[i] = new IntValue();
            carNumbers[i] = new IntValue();
            driverNames[i] = new StringValue();
            gaps[i] = new FloatValue();
        }
        
        
    }
    
    @Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
        int maxNumItems = numVeh.getValue();
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int rowHeight = height / (maxNumItems + 1);
        
        imgTrack.updateSize( width, rowHeight, isEditorMode );
        imgFord.updateSize( width, rowHeight, isEditorMode );
        imgHolden.updateSize( width, rowHeight, isEditorMode );
        Color whiteFontColor = fontColor2.getColor();
        
        dsPos = new DrawnString[maxNumItems];
        dsPosTH = new DrawnString[maxNumItems];
        dsCarNumber = new DrawnString[maxNumItems];
        dsName = new DrawnString[maxNumItems];
        dsTime = new DrawnString[maxNumItems];
        
        
        int top = ( rowHeight - fh ) / 2;
        
        dsTrack = drawnStringFactory.newDrawnString( "dsTrack", width*5/100, top, Alignment.LEFT, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor3.getColor() );
        top += rowHeight;
        
        for(int i=0;i < maxNumItems;i++)
        {
            dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", width*4/200 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor3.getColor() );
            dsPosTH[i] = drawnStringFactory.newDrawnString( "dsPos", width*9/200 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, posFontTH.getFont(), isFontAntiAliased(), fontColor3.getColor() );
            dsCarNumber[i] = drawnStringFactory.newDrawnString( "dsCarNumber", width*25/200 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, f1_2011Font.getFont(), isFontAntiAliased(), fontColor2.getColor() );
            dsName[i] = drawnStringFactory.newDrawnString( "dsName", width*17/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, f1_2011Font.getFont(), isFontAntiAliased(), whiteFontColor );
            dsTime[i] = drawnStringFactory.newDrawnString( "dsTime",  width*97/100 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.RIGHT, false, f1_2011Font.getFont(), isFontAntiAliased(), whiteFontColor );
            
            top += rowHeight;
        }
        
    }
    
    @Override
    protected Boolean updateVisibility( LiveGameData gameData, boolean isEditorMode )
    {
        super.updateVisibility( gameData, isEditorMode );
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int drawncars = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
        initValues();
        
        for(int i=0;i < drawncars;i++)
        { 
            VehicleScoringInfo vsi = scoringInfo.getVehicleScoringInfo( i );
            
            if(vsi != null)
            {
                positions[i].update( vsi.getPlace( false ) );
                carNumbers[i].update( vsi.getVehicleInfo().getCarNumber() );
                driverNames[i].update( vsi.getDriverName() );
                
                
                if(scoringInfo.getSessionType() != SessionType.RACE1)
                    gaps[i].update(vsi.getBestLapTime());
                else
                    gaps[i].update(vsi.getNumPitstopsMade());
                    
                if(carNumbers[i].hasChanged())    
                    forceCompleteRedraw( true );
                
            }
        }
        return true;
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        int maxNumItems = numVeh.getValue();
        int drawncars = Math.min( scoringInfo.getNumVehicles(), maxNumItems );
        int rowHeight = height / (maxNumItems + 1);
        
        texture.clear( imgTrack.getTexture(), offsetX, offsetY, false, null );
        
        for(int i=0;i < drawncars;i++)
        {
            if(scoringInfo.getVehicleScoringInfo( i ).getVehicleInfo().getManufacturer().toUpperCase().equals( "FORD" ) || (isEditorMode && (short)( Math.random() * 2 ) == 0))
                texture.clear( imgFord.getTexture(), offsetX, offsetY+rowHeight*(i+1), false, null );
            else
                texture.clear( imgHolden.getTexture(), offsetX, offsetY+rowHeight*(i+1), false, null );
        }
        
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int drawncars = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
        String SessionName;
        //one time for leader
        
        if ( needsCompleteRedraw || clock.c())
        {
            switch(scoringInfo.getSessionType())
            {
                case RACE1: case RACE2: case RACE3: case RACE4:
                    SessionName = "Race";
                    break;
                case QUALIFYING1: case QUALIFYING2: case QUALIFYING3:  case QUALIFYING4:
                    SessionName = "Qualifying";
                    break;
                case PRACTICE1:
                    SessionName = "Practice 1";
                    break;
                case PRACTICE2:
                    SessionName = "Practice 2";
                    break;
                case PRACTICE3:
                    SessionName = "Practice 3";
                    break;
                case PRACTICE4:
                    SessionName = "Practice 4";
                    break;
                case TEST_DAY:
                    SessionName = "Test";
                    break;
                case WARMUP:
                    SessionName = "Warmup";
                    break;
                default:
                    SessionName = "";
                    break;
                        
            }
            //" Session Classification"
            dsTrack.draw( offsetX, offsetY, gameData.getTrackInfo().getTrackName() + " - " + SessionName, texture);
            
            dsPos[0].draw( offsetX, offsetY, positions[0].getValueAsString(), texture );
            dsPosTH[0].draw( offsetX, offsetY, "ST", texture );
            dsCarNumber[0].draw( offsetX, offsetY, carNumbers[0].getValueAsString(), texture );
            dsName[0].draw( offsetX, offsetY, driverNames[0].getValue(), texture );
            
            if(scoringInfo.getSessionType() == SessionType.RACE1 )
            {
                String stops = ( scoringInfo.getLeadersVehicleScoringInfo().getNumPitstopsMade() > 1 ) ? " Stops" : " Stop";
                dsTime[0].draw( offsetX, offsetY, scoringInfo.getLeadersVehicleScoringInfo().getNumPitstopsMade() + stops, texture);
            }
            else
                if(gaps[0].isValid())
                    dsTime[0].draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(gaps[0].getValue() ) , texture);
                else
                    dsTime[0].draw( offsetX, offsetY, "-:--.---", texture);
        
        
            // the other guys
            for(int i=1;i < drawncars;i++)
            { 
                if ( needsCompleteRedraw || clock.c() )
                {
                    if(positions[i].getValue() == 2)
                        dsPosTH[i].draw( offsetX, offsetY, "ND", texture );
                    else if(positions[i].getValue() == 3)
                        dsPosTH[i].draw( offsetX, offsetY, "RD", texture );
                    else if(positions[i].getValue() >= 10)
                        dsPosTH[i].draw( offsetX, offsetY, "  TH", texture );
                    else
                        dsPosTH[i].draw( offsetX, offsetY, "TH", texture );
                    
                    dsPos[i].draw( offsetX, offsetY, positions[i].getValueAsString(), texture );
                    dsCarNumber[i].draw( offsetX, offsetY, carNumbers[i].getValueAsString(), texture );
                    dsName[i].draw( offsetX, offsetY,driverNames[i].getValue() , texture );  
                    if(scoringInfo.getVehicleScoringInfo( i ).getFinishStatus() == FinishStatus.DQ)
                        dsTime[i].draw( offsetX, offsetY, "DQ", texture);
                    else
                        if(scoringInfo.getSessionType() == SessionType.RACE1 )
                        {
                            if(scoringInfo.getVehicleScoringInfo( i ).getFinishStatus() == FinishStatus.DNF)
                                dsTime[i].draw( offsetX, offsetY, "DNF", texture); 
                            else
                            {
                                String stops = ( scoringInfo.getVehicleScoringInfo( i ).getNumPitstopsMade() > 1 ) ? " Stops" : " Stop";
                                dsTime[i].draw( offsetX, offsetY, scoringInfo.getVehicleScoringInfo( i ).getNumPitstopsMade() + stops, texture);
                            }
                        }
                        else
                            if(!gaps[i].isValid())
                                dsTime[i].draw( offsetX, offsetY, "-:--.---", texture);
                            else
                                if(AbsTimes.getValue() || !gaps[0].isValid())
                                   dsTime[i].draw( offsetX, offsetY, TimingUtil.getTimeAsLaptimeString(gaps[i].getValue() ), texture);
                                else
                                    dsTime[i].draw( offsetX, offsetY,"+ " + TimingUtil.getTimeAsLaptimeString(Math.abs( gaps[i].getValue() - gaps[0].getValue() )) , texture);
                 }
                
            }
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( f1_2011Font, "" );
        writer.writeProperty( posFontTH, "" );
        writer.writeProperty( fontColor3, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( numVeh, "" );
        writer.writeProperty( AbsTimes, "" );
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( fontxposoffset, "" );
        writer.writeProperty( fontxnameoffset, "" );
        writer.writeProperty( fontxtimeoffset, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( f1_2011Font ) );
        else if ( loader.loadProperty( posFontTH ) );
        else if ( loader.loadProperty( fontColor3 ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( numVeh ) );
        else if ( loader.loadProperty( AbsTimes ) );
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( fontxposoffset ) );
        else if ( loader.loadProperty( fontxnameoffset ) );
        else if ( loader.loadProperty( fontxtimeoffset ) );
    }
    
    @Override
    protected void addFontPropertiesToContainer( PropertiesContainer propsCont, boolean forceAll )
    {
        propsCont.addGroup( "Colors and Fonts" );
        
        super.addFontPropertiesToContainer( propsCont, forceAll );
        propsCont.addProperty( f1_2011Font );
        propsCont.addProperty( posFontTH );
        propsCont.addProperty( fontColor3 );
        propsCont.addProperty( fontColor2 );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
        
        propsCont.addProperty( numVeh );
        propsCont.addProperty( AbsTimes );
        propsCont.addGroup( "Font Displacement" );
        propsCont.addProperty( fontyoffset );
        propsCont.addProperty( fontxposoffset );
        propsCont.addProperty( fontxnameoffset );
        propsCont.addProperty( fontxtimeoffset );
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
    
    public ResultMonitorWidget()
    {
        super( PrunnWidgetSetv8.INSTANCE, PrunnWidgetSetv8.WIDGET_PACKAGE_V8, 66.4f, 46.5f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetv8.V8_FONT_NAME );
        getFontColorProperty().setColor( PrunnWidgetSetv8.FONT_COLOR1_NAME );
    }
}
