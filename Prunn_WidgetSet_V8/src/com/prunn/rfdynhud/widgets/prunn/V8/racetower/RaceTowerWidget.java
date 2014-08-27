package com.prunn.rfdynhud.widgets.prunn.V8.racetower;

import java.awt.Font;
import java.io.IOException;

import net.ctdp.rfdynhud.gamedata.GamePhase;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
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
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.util.TimingUtil;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;
import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetv8;
import com.prunn.rfdynhud.widgets.prunn.V8.raceinfos.RaceInfosWidget;

/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class RaceTowerWidget extends Widget
{
    public static Boolean isvisible = false;
    public static Boolean visible()
    {
        return isvisible;
    }
    private TextureImage2D texGainedPlaces = null;
    private final ImagePropertyWithTexture imgPositive = new ImagePropertyWithTexture( "imgTime", "prunn/V8/pospositive.png" );
    private final ImagePropertyWithTexture imgNegative = new ImagePropertyWithTexture( "imgTime", "prunn/V8/posnegative.png" );
    private final ImagePropertyWithTexture imgNeutral = new ImagePropertyWithTexture( "imgTime", "prunn/V8/posneutral.png" );
    
    private final ImagePropertyWithTexture imgFord = new ImagePropertyWithTexture( "imgPos", "prunn/V8/race_leaderboard_ford.png" );
    private final ImagePropertyWithTexture imgHolden = new ImagePropertyWithTexture( "imgPos", "prunn/V8/race_leaderboard_holden.png" );
    private final FontProperty posFontTH = new FontProperty("v8thFont", PrunnWidgetSetv8.V8_FONT_NAME_TH);
    private final ColorProperty fontColor3 = new ColorProperty("fontColor3", PrunnWidgetSetv8.FONT_COLOR3_NAME);
    private final ColorProperty fontColor2 = new ColorProperty( "fontColor2", PrunnWidgetSetv8.FONT_COLOR2_NAME );
    private final DelayProperty visibleTime = new DelayProperty( "visibleTime", DelayProperty.DisplayUnits.SECONDS, 12 );
    private long visibleEnd = 0;
    private DrawnString[] dsPos = null;
    private DrawnString[] dsPosTH = null;
    private DrawnString[] dsNumber = null;
    private DrawnString[] dsName = null;
    private DrawnString[] dsTime = null;
    private DrawnString[] dsPit = null;
    private DrawnString[] dsPitLabel = null;
    private IntProperty fontyoffset = new IntProperty("Y Font Offset", 0);
    private IntProperty fontxposoffset = new IntProperty("X Position Font Offset", 0);
    private IntProperty fontxnameoffset = new IntProperty("X Name Font Offset", 0);
    private IntProperty fontxtimeoffset = new IntProperty("X Time Font Offset", 0);
    private final IntProperty numVeh = new IntProperty( "numberOfVehicles", 4 );
    private IntProperty randMulti = new IntProperty("Show Multiplier", 0);
    private int[] startedPositions = null;
    private final IntValue currentLap = new IntValue();
    private short shownData = 0; //0-2-4-gaps 1-place gained
    private final IntValue drawnCars = new IntValue();
    private final IntValue carsOnLeadLap = new IntValue();
    private boolean startedPositionsInitialized = false;
    private short[] positions = null;
    private int[] number = null;
    private int[] pit = null;
    private short[] gainedPlaces = null;
    private String[] names = null;
    private String[] gaps = null;
    
   
    @Override
    public void onCockpitEntered( LiveGameData gameData, boolean isEditorMode )
    {
        super.onCockpitEntered( gameData, isEditorMode );
        String cpid = "Y29weXJpZ2h0QFBydW5uMjAxMQ";
        if(!isEditorMode)
            log(cpid);
        drawnCars.reset();
        visibleEnd = 0;
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
        dsPit = new DrawnString[maxNumItems];
        dsPitLabel = new DrawnString[maxNumItems];
                
        int fh = TextureImage2D.getStringHeight( "0%C", getFontProperty() );
        int rowHeight = height / maxNumItems;
        
        imgFord.updateSize( width, rowHeight, isEditorMode );
        imgHolden.updateSize( width, rowHeight, isEditorMode );
        
        int top = ( rowHeight - fh ) / 2;
        
        for(int i=0;i < maxNumItems;i++)
        { 
            dsPos[i] = drawnStringFactory.newDrawnString( "dsPos", width*1/100 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, getFont(), isFontAntiAliased(), fontColor3.getColor() );
            dsPosTH[i] = drawnStringFactory.newDrawnString( "dsPos", width*4/100 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, posFontTH.getFont(), isFontAntiAliased(), fontColor3.getColor() );
            dsNumber[i] = drawnStringFactory.newDrawnString( "dsNumber", width*13/100 + fontxposoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), fontColor2.getColor() );
            dsName[i] = drawnStringFactory.newDrawnString( "dsName", width*19/100 + fontxnameoffset.getValue(), top + fontyoffset.getValue(), Alignment.LEFT, false, getFont(), isFontAntiAliased(), fontColor3.getColor() );
            dsTime[i] = drawnStringFactory.newDrawnString( "dsTime", width*84/100 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), fontColor2.getColor() );
            dsPit[i] = drawnStringFactory.newDrawnString( "dsTime", width*98/100 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.RIGHT, false, getFont(), isFontAntiAliased(), fontColor2.getColor() );
            dsPitLabel[i] = drawnStringFactory.newDrawnString( "dsTime", width*181/200 + fontxtimeoffset.getValue(), top + fontyoffset.getValue(), Alignment.CENTER, false, getFont(), isFontAntiAliased(), fontColor3.getColor() );
            
            top += rowHeight;
        }
        
        
    }
    private void clearArrayValues(int maxNumCars)
    {
        positions = new short[maxNumCars];
        number = new int[maxNumCars];
        pit = new int[maxNumCars];
        gainedPlaces = new short[maxNumCars];
        gaps = new String[maxNumCars];
        names = new String[maxNumCars];
        
        for(int i=0;i<maxNumCars;i++)
        {
            positions[i] = -1;
            number[i] = -1;
            pit[i] = -1;
            gainedPlaces[i] = 0;
            gaps[i] = "";
            names[i] = "";
        }
    }
    private void FillArrayValues(int onLeaderLap, ScoringInfo scoringInfo, boolean isEditorMode, LiveGameData gameData)
    {
        if(isEditorMode)
        {
            //data = 4;
            onLeaderLap = numVeh.getValue();
        }
        int negrand[] = new int[2];
        negrand[0] = 1;
        negrand[1] = -1;
        
        for(int i=0;i<onLeaderLap;i++)
        {
            
            if(positions[i] == -1)
            {
                
            
                VehicleScoringInfo vsi = scoringInfo.getVehicleScoringInfo( i );
                positions[i] = vsi.getPlace( false );
                number[i] = vsi.getVehicleInfo().getCarNumber();
                names[i] = ShortName(vsi.getDriverName().toUpperCase());
                if(i == 0)    
                    gaps[i] = "";
                else
                    gaps[i] = TimingUtil.getTimeAsGapString( -vsi.getTimeBehindLeader( false ));
                
                switch(shownData) //0-2-4-gaps 1-place gained
                {
                    case 1: //places
                            int startedfrom=0;
                            for(int p=0; p < scoringInfo.getNumVehicles(); p++)
                            {
                                if( vsi.getDriverId() == startedPositions[p] )
                                {
                                    startedfrom = p+1;
                                    break;
                                } 
                            }
                            if(isEditorMode)
                            {
                                gainedPlaces[i] = (short)(( Math.random() * 2 )*negrand[(short)(Math.random() * 2)] );
                                pit[i] = Math.abs( gainedPlaces[i]) ;
                                
                            }
                            else
                            {
                                gainedPlaces[i] = (short)( startedfrom - vsi.getPlace( false ) );
                                pit[i] = Math.abs( startedfrom - vsi.getPlace( false ));
                            }
                            break;
                    default: //pit
                            pit[i] = vsi.getNumPitstopsMade();
                        
                            break;
                }
            }
        }
    }
    private void initStartedFromPositions( ScoringInfo scoringInfo )
    {
        startedPositions = new int[scoringInfo.getNumVehicles()];
        
        for(int j=0;j < scoringInfo.getNumVehicles(); j++)
            startedPositions[j] = scoringInfo.getVehicleScoringInfo( j ).getDriverId();
        
        startedPositionsInitialized = true;
    }
    
    @Override
    protected Boolean updateVisibility( LiveGameData gameData, boolean isEditorMode )
    {
        super.updateVisibility( gameData, isEditorMode );
        
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        
        if ( !startedPositionsInitialized )
            initStartedFromPositions( scoringInfo );
        if(RaceInfosWidget.visible())
                return false;
        
        currentLap.update( scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() );
        
        if( currentLap.hasChanged() && currentLap.getValue() > 0 && (short)( Math.random() * randMulti.getValue()) == 0 || isEditorMode)
        {
            
            //fetch what data is shown others-gaps 1-places gained/lost
            if(scoringInfo.getLeadersVehicleScoringInfo().getFinishStatus().isFinished() || isEditorMode)
                shownData = 1 ;
            else
                shownData = (short)( Math.random() * 2 );
            
            if(scoringInfo.getGamePhase() == GamePhase.SESSION_OVER)
                visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos()*3;
            else
                visibleEnd = scoringInfo.getSessionNanos() + visibleTime.getDelayNanos();
            
            clearArrayValues(scoringInfo.getNumVehicles());
            FillArrayValues( 1, scoringInfo, isEditorMode, gameData);
            if(!isEditorMode)
                forceCompleteRedraw( true );
            
            return true;
            
        }
        
        if(scoringInfo.getSessionNanos() < visibleEnd || isEditorMode)
        {
            //how many on the same lap?
            int onlap = 0;
            for(int j=0;j < scoringInfo.getNumVehicles(); j++)
            {
                if(scoringInfo.getVehicleScoringInfo( j ).getLapsCompleted() == scoringInfo.getLeadersVehicleScoringInfo().getLapsCompleted() )
                    onlap++;
            }
                
            carsOnLeadLap.update( onlap );
            if (carsOnLeadLap.hasChanged() && !isEditorMode )
            {
                FillArrayValues( onlap, scoringInfo, false, gameData);
                forceCompleteRedraw( true );
            }
            return true;
        }
        
        
        return false;
    }
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        //logCS("test");
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        int maxNumItems = numVeh.getValue();
        int rowHeight = height / maxNumItems;
        int drawncars = Math.min( scoringInfo.getNumVehicles(), maxNumItems );
        short posOffset;
        
                
        for(int i=0;i < drawncars;i++)
        {
            if(carsOnLeadLap.getValue() > numVeh.getValue() && i != 0)
                posOffset = (short)( carsOnLeadLap.getValue() - numVeh.getValue() );
            else
                posOffset = 0;
        
            if(positions[i + posOffset] != -1 || isEditorMode)
            {
                
                
                if(scoringInfo.getVehicleScoringInfo( positions[i + posOffset] - 1).getVehicleInfo().getManufacturer().toUpperCase().equals( "FORD" ) || (isEditorMode && (short)( Math.random() * 2 ) == 0))
                    texture.clear( imgFord.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
                else
                    texture.clear( imgHolden.getTexture(), offsetX, offsetY+rowHeight*i, false, null );
            
            
                if( shownData == 1)
                {
                    if(gainedPlaces[i + posOffset] > 0)
                        texGainedPlaces = imgPositive.getImage().getScaledTextureImage( width*9/100, rowHeight, texGainedPlaces, isEditorMode );
                    else 
                        if(gainedPlaces[i + posOffset] < 0)
                            texGainedPlaces = imgNegative.getImage().getScaledTextureImage( width*9/100, rowHeight, texGainedPlaces, isEditorMode );
                        else
                            texGainedPlaces = imgNeutral.getImage().getScaledTextureImage( width*9/100, rowHeight, texGainedPlaces, isEditorMode );
                    
                    texture.drawImage( texGainedPlaces, offsetX + width*171/200, offsetY+rowHeight*i, true, null );
                    
                }
            }
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
        
        if ( needsCompleteRedraw)
        {
            int drawncars = Math.min( scoringInfo.getNumVehicles(), numVeh.getValue() );
            short posOffset;
            
            
            for(int i=0;i < drawncars;i++)
            { 
                if(carsOnLeadLap.getValue() > numVeh.getValue() && i != 0)
                    posOffset = (short)( carsOnLeadLap.getValue() - numVeh.getValue() );
                else
                    posOffset = 0;
                
                if(positions[i + posOffset] != -1)
                {
                    if(positions[i + posOffset] == 1)
                        dsPosTH[i].draw( offsetX, offsetY, "ST", texture );
                    else if(positions[i + posOffset] == 2)
                        dsPosTH[i].draw( offsetX, offsetY, "ND", texture );
                    else if(positions[i + posOffset] == 3)
                        dsPosTH[i].draw( offsetX, offsetY, "RD", texture );
                    else if(positions[i + posOffset] >= 10)
                        dsPosTH[i].draw( offsetX, offsetY, "  TH", texture );
                    else
                        dsPosTH[i].draw( offsetX, offsetY, "TH", texture );
                    
                    dsPos[i].draw( offsetX, offsetY, String.valueOf(positions[i + posOffset]), texture );
                    dsNumber[i].draw( offsetX, offsetY, String.valueOf(number[i + posOffset]), texture );
                    dsPit[i].draw( offsetX, offsetY, String.valueOf(pit[i + posOffset]), texture );
                    if( shownData != 1)
                        dsPitLabel[i].draw( offsetX, offsetY, "PIT", texture );
                }
                /*else
                {
                    dsPos[i].draw( offsetX, offsetY, "", texture );
                    dsNumber[i].draw( offsetX, offsetY, "", texture );
                }*/
                
                
                dsName[i].draw( offsetX, offsetY, names[i + posOffset], texture );
                dsTime[i].draw( offsetX, offsetY, gaps[i + posOffset], texture );
            }
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        
        writer.writeProperty( fontColor2, "" );
        writer.writeProperty( fontColor3, "" );
        writer.writeProperty( posFontTH, "" );
        writer.writeProperty( numVeh, "" );
        writer.writeProperty( visibleTime, "visibleTime" );
        writer.writeProperty( randMulti, "ShowMultiplier" );
        writer.writeProperty( fontyoffset, "" );
        writer.writeProperty( fontxposoffset, "" );
        writer.writeProperty( fontxnameoffset, "" );
        writer.writeProperty( fontxtimeoffset, "" );
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        
        if ( loader.loadProperty( fontColor2 ) );
        else if ( loader.loadProperty( fontColor3 ) );
        else if ( loader.loadProperty( posFontTH ) );
        else if ( loader.loadProperty( numVeh ) );
        else if ( loader.loadProperty( visibleTime ) );
        else if ( loader.loadProperty( randMulti ) );
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
        propsCont.addProperty( posFontTH );
        propsCont.addProperty( fontColor2 );
        propsCont.addProperty( fontColor3 );
        
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Specific" );
        propsCont.addProperty( numVeh );
        propsCont.addProperty( visibleTime );
        propsCont.addProperty( randMulti );
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
    
    public RaceTowerWidget()
    {
        super( PrunnWidgetSetv8.INSTANCE, PrunnWidgetSetv8.WIDGET_PACKAGE_V8, 20.0f, 32.5f );
        
        getBackgroundProperty().setColorValue( "#00000000" );
        getFontProperty().setFont( PrunnWidgetSetv8.V8_FONT_NAME );
    }
}
