package com.prunn.rfdynhud.widgets.prunn.V8.qualtimingtower;

import java.awt.Font;
import java.io.IOException;

import net.ctdp.rfdynhud.gamedata.FinishStatus;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.input.InputAction;
import net.ctdp.rfdynhud.properties.BooleanProperty;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.DelayProperty;
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
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.BoolValue;
import net.ctdp.rfdynhud.values.FloatValue;
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


public class QualifTimingTowerWidget extends Widget
{
    private DrawnString[] dsPos = null;
    private DrawnString[] dsPosTH = null;
    private DrawnString[] dsName = null;
    private DrawnString[] dsTime = null;
    private final ImagePropertyWithTexture imgPosFord = new ImagePropertyWithTexture( "imgPosFord", "prunn/V8/tower_ford.png" );
    private final ImagePropertyWithTexture imgPosFordNew = new ImagePropertyWithTexture( "imgPosNew", "prunn/V8/tower_ford_newlap.png" );
    private final ImagePropertyWithTexture imgPosHolden = new ImagePropertyWithTexture( "imgPosKnockOut", "prunn/V8/tower_holden.png" );
    private final ImagePropertyWithTexture imgPosHoldenNew = new ImagePropertyWithTexture( "imgPosNewKnockOut", "prunn/V8/tower_holden_newlap.png" );
    
    protected final FontProperty V8Font = new FontProperty("Main Font", PrunnWidgetSetv8.V8_FONT_NAME);
    protected final FontProperty V8THFont = new FontProperty("Main Font", PrunnWidgetSetv8.V8_FONT_NAME_TH);
    private final ColorProperty fontColor3 = new ColorProperty( "fontColor3", PrunnWidgetSetv8.FONT_COLOR3_NAME );
    private final ColorProperty fontColor2 = new ColorProperty( "fontColor2", PrunnWidgetSetv8.FONT_COLOR2_NAME );
    
    private final IntProperty numVeh = new IntProperty( "numberOfVehicles", 8 );
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    private IntProperty fontxposTHoffset = new IntProperty("X Position TH Offset", 0);
    private IntProperty fontxposoffset = new IntProperty("X Position Font Offset", 0);
    private IntProperty fontxnameoffset = new IntProperty("X Name Font Offset", 0);
    private IntProperty fontxtimeoffset = new IntProperty("X Time Font Offset", 0);
    
    private final DelayProperty visibleTime = new DelayProperty( "visibleTime", DelayProperty.DisplayUnits.SECONDS, 5 );
    private final DelayProperty visibleTimeButton = new DelayProperty( "visibleTimeButton", DelayProperty.DisplayUnits.SECONDS, 15 );
    private long visibleEnd = -1L;
    private long[] visibleEndArray;
    
    private VehicleScoringInfo[] vehicleScoringInfos;
    private IntValue[] positions = null;
    private final IntValue numValid = new IntValue();
    private StringValue[] driverNames = null;
    private int[] Manufacturer = null;
    private FloatValue[] gaps = null;
    private BoolValue[] IsInPit = null;
    private BoolValue[] IsFinished = null;
    private int[] driverIDs = null;
    private boolean[] gapFlag = null;
    private boolean[] gapFlag2 = null;
    private static final InputAction showTimes = new InputAction( "Show times", true );
    private final IntValue inputShowTimes = new IntValue();
    private BooleanProperty AbsTimes = new BooleanProperty("Use absolute times", false) ;
    
    private static final String getTimeAsGapString2( float gap )
    {
        if ( gap == 0f )
            return ( "-0.0000");
        
        if ( gap < 0f )
            return ( "-" + String.valueOf( -gap ).substring( 0, 6 ) );
        
        return ( "+" + String.valueOf( gap ).substring( 0, 6 ) );
    }
    private static final String getTimeAsString2( float time )
    {
        String rebuild;
        if(time >= 60)
            rebuild = String.valueOf( (int) time/60 ) + ":";
        else
            rebuild = "0:";
        
        if(time%60 >= 10)
            rebuild += String.valueOf(time%60);
        else
            rebuild += "0" + String.valueOf(time%60);
        
        if(rebuild.length() >= 9)
            return ( rebuild.substring( 0, 9 ) );
        else
        {
            for(int i=rebuild.length(); i < 9; i++)
            {
                rebuild += "0";
            }
            return ( rebuild );
        }
    }
    @Override
    public InputAction[] getInputActions()
    {
        return ( new InputAction[] { showTimes } );
    }
    @Override
    public void onCockpitEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onCockpitEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
        visibleEnd = -1L;
        numValid.reset();
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    protected Boolean onBoundInputStateChanged( InputAction action, boolean state, int modifierMask, long when, LiveGameData gameData, boolean isEditorMode )
    {
        Boolean result = super.onBoundInputStateChanged( action, state, modifierMask, when, gameData, isEditorMode );
        int maxNumItems = numVeh.getValue();
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        if ( action == showTimes )
        {
            for(int i = 1; i < maxNumItems;i++)
                visibleEndArray[i] = scoringInfo.getSessionNanos() + visibleTimeButton.getDelayNanos();
            
           inputShowTimes.update( inputShowTimes.getValue()+1 );
        }
        
        return ( result );
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
        gapFlag = new boolean[maxNumItems];
        gapFlag2 = new boolean[maxNumItems];
        positions = new IntValue[maxNumItems];
        driverNames = new StringValue[maxNumItems];
        Manufacturer = new int[maxNumItems];
        IsInPit = new BoolValue[maxNumItems];
        IsFinished = new BoolValue[maxNumItems];
        driverIDs = new int[maxNumItems];
        visibleEndArray = new long[maxNumItems];
        vehicleScoringInfos = new VehicleScoringInfo[maxNumItems];
        
        for(int i=0;i < maxNumItems;i++)
        { 
            IsInPit[i] = new BoolValue();
            IsFinished[i] = new BoolValue();
            positions[i] = new IntValue();
            driverNames[i] = new StringValue();
            gaps[i] = new FloatValue();
        }
        
        
    }
    
    @Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
        int maxNumItems = numVeh.getValue();
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int rowHeight = height / maxNumItems;
        
        imgPosFordNew.updateSize( width, rowHeight, isEditorMode );
        imgPosFord.updateSize( width*92/200, rowHeight, isEditorMode );
        imgPosHolden.updateSize( width*92/200, rowHeight, isEditorMode );
        imgPosHoldenNew.updateSize( width, rowHeight, isEditorMode );
        
        dsPos = new DrawnString[maxNumItems];
        dsPosTH = new DrawnString[maxNumItems];
        dsName = new DrawnString[maxNumItems];
        dsTime = new DrawnString[maxNumItems];
        
        int top = ( rowHeight - fh ) / 2;
        
        for(int i=0;i < maxNumItems;i++)
        {
            dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", width*2/100 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, V8Font.getFont(), isFontAntiAliased(), fontColor3.getColor() );
            dsPosTH[i] = drawnStringFactory.newDrawnString( "dsPosTH", width*9/100 + fontxposoffset.getValue() + fontxposTHoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, V8THFont.getFont(), isFontAntiAliased(), fontColor3.getColor() );
            dsName[i] = drawnStringFactory.newDrawnString( "dsName", width*27/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, V8Font.getFont(), isFontAntiAliased(), fontColor2.getColor() );
            dsTime[i] = drawnStringFactory.newDrawnString( "dsTime",  width*98/100 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.RIGHT, false, V8Font.getFont(), isFontAntiAliased(), fontColor3.getColor() );
            
            top += rowHeight;
        }
        
    }
    
    @Override
    protected Boolean updateVisibility( LiveGameData gameData, boolean isEditorMode )
    {
        super.updateVisibility( gameData, isEditorMode );
        
        initValues();
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        int drawncars = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
        VehicleScoringInfo  comparedVSI = scoringInfo.getViewedVehicleScoringInfo();
        
        if(inputShowTimes.hasChanged())
            forceCompleteRedraw( true ); 
        if(scoringInfo.getViewedVehicleScoringInfo().getBestLapTime() > 0)
        {
            if(scoringInfo.getViewedVehicleScoringInfo().getPlace( false ) > numVeh.getValue())
                comparedVSI = scoringInfo.getVehicleScoringInfo( scoringInfo.getViewedVehicleScoringInfo().getPlace( false ) - 5 );
            else
                comparedVSI = scoringInfo.getLeadersVehicleScoringInfo();
        
        }
        else
        {
            comparedVSI = scoringInfo.getLeadersVehicleScoringInfo();
            /*for(int i=drawncars-1; i >= 0; i--)
            {
                if(scoringInfo.getVehicleScoringInfo( i ).getBestLapTime() > 0)
                {
                    comparedVSI = scoringInfo.getVehicleScoringInfo( i ); 
                    break;
                }
            }*/
        }

        StandingsTools.getDisplayedVSIsForScoring(scoringInfo, comparedVSI, false, StandingsView.RELATIVE_TO_LEADER, true, vehicleScoringInfos);
        
        for(int i=0;i < drawncars;i++)
        { 
            VehicleScoringInfo vsi = vehicleScoringInfos[i];
            
            if(vsi != null && vsi.getFinishStatus() != FinishStatus.DQ)
            {

                positions[i].update( vsi.getPlace( false ) );
                if(vsi.getDriverNameTLC( true ).substring( 1, 2 ).equals( "." ))
                    driverNames[i].update(vsi.getDriverNameTLC( true ).substring( 0, 1 ) + vsi.getDriverNameTLC( true ).substring( 2, 3 ) );
                else
                    driverNames[i].update(vsi.getDriverNameTLC( true ).substring( 0, 2 ));
                
                if(vsi.getVehicleInfo().getManufacturer().toUpperCase().equals( "FORD" ) )
                    Manufacturer[i] = 0;
                else
                    Manufacturer[i] = 1;
                
                if(isEditorMode && (short)( Math.random() * 2 ) == 0)
                    Manufacturer[i] = 0;
                    
                IsInPit[i].update( vsi.isInPits() );
                
                if(vsi.getFinishStatus() == FinishStatus.FINISHED)
                    IsFinished[i].update( true );
                else
                    IsFinished[i].update( false );
                
                gaps[i].setUnchanged();
                gaps[i].update(vsi.getBestLapTime());
                gapFlag[i] = gaps[i].hasChanged( false ) || isEditorMode;
                gapFlag2[i] = gapFlag[i];// || gapFlag2[i];
                if((IsInPit[i].hasChanged() || IsFinished[i].hasChanged()) && !isEditorMode)
                    forceCompleteRedraw( true );  
            }
        }
        
        if((scoringInfo.getSessionNanos() >= visibleEnd) && (visibleEnd != -1L))
        {
            visibleEnd = -1L;
            if ( !isEditorMode )
                forceCompleteRedraw( true );
        }
        
        if(!gaps[0].isValid())
            visibleEnd = -1L;
        else if(gapFlag[0])
            visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
        
        for(int i=1;i < drawncars;i++)
        {
            if(gaps[i].isValid())
            {
                if(gapFlag[i] && !isEditorMode )
                {
                    //search if the time really changed or just the position before redrawing
                    for(int j=0;j < drawncars; j++)
                    {
                        if ( vehicleScoringInfos[i].getDriverId() == driverIDs[j] )
                        {
                            if(gaps[i].getValue() == gaps[j].getOldValue())
                            {
                                gapFlag[i] = false;
                                break;
                            }
                        }
                    }
                }
                
                if((scoringInfo.getSessionNanos() >= visibleEndArray[i]) && (visibleEndArray[i] != -1L))
                {
                    visibleEndArray[i] = -1L;
                    if ( !isEditorMode )
                        forceCompleteRedraw( true );
                }
                
                if(gapFlag[i]) 
                {
                    visibleEndArray[i] = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
                    if ( !isEditorMode )
                        forceCompleteRedraw( true );
                }
            }
        }
        
        for(int i=0;i < drawncars;i++)
        { 
            VehicleScoringInfo vsi = vehicleScoringInfos[i];
            
            if(vsi != null)
            {
                driverIDs[i] = vsi.getDriverId();
            }
        }
        
        int nv = 0;
        for(int i=0;i < drawncars;i++)
        {
            if(gaps[i].isValid())
                nv++;
        }
        
        numValid.update( nv );
        if ( numValid.hasChanged() && !isEditorMode )
            forceCompleteRedraw( true );
        
        if( gameData.getScoringInfo().getLeadersVehicleScoringInfo().getBestLapTime() > 0 || isEditorMode)
        {
            return true;
        }
        
        return false;
        
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        int maxNumItems = numVeh.getValue();
        int drawncars = Math.min( scoringInfo.getNumVehicles(), maxNumItems );
        int rowHeight = height / maxNumItems;
        
        if(gaps[0].isValid())
        {
            if(Manufacturer[0] == 0)
                texture.clear( imgPosFordNew.getTexture(), offsetX, offsetY, false, null );
            else
                texture.clear( imgPosHoldenNew.getTexture(), offsetX, offsetY, false, null );
        }
        
        
        for(int i=1;i < drawncars;i++)
        {
            if(gaps[i].isValid())
            {
                if(scoringInfo.getSessionNanos() < visibleEndArray[i] && !isEditorMode)
                {
                    if(Manufacturer[i] == 0)
                        texture.clear( imgPosFordNew.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                    else
                        texture.clear( imgPosHoldenNew.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                }
                else  
                    {
                        if(Manufacturer[i] == 0)
                            texture.clear( imgPosFord.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                        else
                            texture.clear( imgPosHolden.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                    }
            }
            
        }
        
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        int drawncars = Math.min( gameData.getScoringInfo().getNumVehicles(), numVeh.getValue() );
        
        //one time for leader
        
        if ( needsCompleteRedraw || ( clock.c() && gapFlag2[0]))
        {
            if(gaps[0].isValid())
            {
                //VehicleScoringInfo vsi = vehicleScoringInfos[0];
                
                dsPosTH[0].draw( offsetX, offsetY, "ST", texture );
                dsPos[0].draw( offsetX, offsetY, positions[0].getValueAsString(), texture );
                dsName[0].draw( offsetX, offsetY, driverNames[0].getValue(), texture );
                dsTime[0].draw( offsetX, offsetY, getTimeAsString2(gaps[0].getValue() ) , texture);
            }
            else
            {
                dsTime[0].draw( offsetX, offsetY, "" , texture);
            }
            
            gapFlag2[0] = false;
            
        }
        
        // the other guys
        for(int i=1;i < drawncars;i++)
        { 
            if ( needsCompleteRedraw || ( clock.c() && gapFlag2[i]))
            {
                if(gaps[i].isValid())
                {
                    
                    if(positions[i].getValue() == 2)
                        dsPosTH[i].draw( offsetX, offsetY, "ND", texture );
                    else if(positions[i].getValue() == 3)
                        dsPosTH[i].draw( offsetX, offsetY, "RD", texture );
                    else if(positions[i].getValue() >= 10)
                        dsPosTH[i].draw( offsetX, offsetY, "   TH", texture );
                    else
                        dsPosTH[i].draw( offsetX, offsetY, "TH", texture );
                    
                    dsPos[i].draw( offsetX, offsetY, positions[i].getValueAsString(), texture );
                    dsName[i].draw( offsetX, offsetY,driverNames[i].getValue() , texture );  
                    
                    if(gameData.getScoringInfo().getSessionNanos() < visibleEndArray[i])
                    {
                        if(AbsTimes.getValue())
                            dsTime[i].draw( offsetX, offsetY, getTimeAsString2(gaps[i].getValue() ), texture);
                        else
                            dsTime[i].draw( offsetX, offsetY,"+ " + getTimeAsGapString2(Math.abs( gaps[i].getValue() - gaps[0].getValue() )) , texture);
                    }
                    else
                        dsTime[i].draw( offsetX, offsetY,"", texture);
                    
                    
                }
                
                gapFlag2[i] = false;
                
                
            }
            
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( V8Font, "" );
        writer.writeProperty( V8THFont, "" );
        writer.writeProperty( fontColor3, "" );
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( numVeh, "" );
        writer.writeProperty( visibleTime, "" );
        writer.writeProperty( visibleTimeButton, "" );
        writer.writeProperty( AbsTimes, "" );
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( fontxposoffset, "" );
        writer.writeProperty( fontxposTHoffset, "" );
        writer.writeProperty( fontxnameoffset, "" );
        writer.writeProperty( fontxtimeoffset, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( V8Font ) );
        else if ( loader.loadProperty( V8THFont ) );
        else if ( loader.loadProperty( fontColor3 ) );
        else if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( numVeh ) );
        else if ( loader.loadProperty( visibleTime ) );
        else if ( loader.loadProperty( visibleTimeButton ) );
        else if ( loader.loadProperty( AbsTimes ) );
        else if ( loader.loadProperty( fontyoffset ) );
        else if ( loader.loadProperty( fontxposoffset ) );
        else if ( loader.loadProperty( fontxposTHoffset ) );
        else if ( loader.loadProperty( fontxnameoffset ) );
        else if ( loader.loadProperty( fontxtimeoffset ) );
    }
    
    @Override
    protected void addFontPropertiesToContainer( PropertiesContainer propsCont, boolean forceAll )
    {
        propsCont.addGroup( "Colors and Fonts" );
        
        super.addFontPropertiesToContainer( propsCont, forceAll );
        propsCont.addProperty( V8Font );
        propsCont.addProperty( V8THFont );
        propsCont.addProperty( fontColor3 );
        propsCont.addProperty( fontColor2 );
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
        
        propsCont.addProperty( numVeh );
        propsCont.addProperty( visibleTime );
        propsCont.addProperty( visibleTimeButton );
        propsCont.addProperty( AbsTimes );
        propsCont.addGroup( "Font Displacement" );
        propsCont.addProperty( fontyoffset );
        propsCont.addProperty( fontxposoffset );
        propsCont.addProperty( fontxposTHoffset );
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
    
    public QualifTimingTowerWidget()
    {
        super( PrunnWidgetSetv8.INSTANCE, PrunnWidgetSetv8.WIDGET_PACKAGE_V8, 22.5f, 32.5f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetv8.V8_FONT_NAME );
        getFontColorProperty().setColor( PrunnWidgetSetv8.FONT_COLOR1_NAME );
    }
}
