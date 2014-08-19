package com.prunn.rfdynhud.widgets.prunn.V8.revmetter;

import java.awt.Font;
import java.io.IOException;

import com.prunn.rfdynhud.widgets.prunn._util.PrunnWidgetSetv8;
import net.ctdp.rfdynhud.gamedata.LiveGameData;
import net.ctdp.rfdynhud.gamedata.ScoringInfo;
import net.ctdp.rfdynhud.gamedata.TelemetryData;
import net.ctdp.rfdynhud.gamedata.VehicleScoringInfo;
import net.ctdp.rfdynhud.properties.BooleanProperty;
import net.ctdp.rfdynhud.properties.ColorProperty;
import net.ctdp.rfdynhud.properties.FontProperty;
import net.ctdp.rfdynhud.properties.ImageProperty;
import net.ctdp.rfdynhud.properties.ImagePropertyWithTexture;
import net.ctdp.rfdynhud.properties.PropertiesContainer;
import net.ctdp.rfdynhud.properties.PropertyLoader;
import net.ctdp.rfdynhud.render.DrawnString;
import net.ctdp.rfdynhud.render.DrawnStringFactory;
import net.ctdp.rfdynhud.render.ImageTemplate;
import net.ctdp.rfdynhud.render.TextureImage2D;
import net.ctdp.rfdynhud.render.TransformableTexture;
import net.ctdp.rfdynhud.render.DrawnString.Alignment;
import net.ctdp.rfdynhud.util.FontUtils;
import net.ctdp.rfdynhud.util.PropertyWriter;
import net.ctdp.rfdynhud.util.SubTextureCollector;
import net.ctdp.rfdynhud.valuemanagers.Clock;
import net.ctdp.rfdynhud.values.IntValue;
import net.ctdp.rfdynhud.widgets.base.widget.Widget;



/**
 * @author Prunn
 * copyright@Prunn2011
 * 
 */


public class RevMetterWidget extends Widget
{
    private TextureImage2D texGear = null;
    private DrawnString dsName = null;
    private DrawnString dsSpeed = null;
    private DrawnString dsSpeedUnit = null;
    private DrawnString dsRPM = null;
    private DrawnString dsRPMUnit = null;
    private DrawnString dsTrack = null;
    private DrawnString dsTrackTemp = null;
    private DrawnString dsBrake = null;
    private DrawnString dsGear1 = null;
    private DrawnString dsGear2 = null;
    private DrawnString dsGear3 = null;
    private DrawnString dsGear4 = null;
    private DrawnString dsGear5 = null;
    private DrawnString dsGear6 = null;
    protected final FontProperty V8Font = new FontProperty("Main Font", PrunnWidgetSetv8.V8_FONT_NAME);
    protected final FontProperty speedUnitFont = new FontProperty("speed Unit Font", "speedUnitFont");
    protected final FontProperty speedF11Font = new FontProperty("speed Font", "speedF11Font");
    protected final FontProperty gearF11Font = new FontProperty("gear Font", "gearF11Font");
    protected final FontProperty TBFont = new FontProperty("TBFont", "TBFont");
    private final ColorProperty fontColor3 = new ColorProperty( "fontColor3", PrunnWidgetSetv8.FONT_COLOR3_NAME );
    private final ColorProperty speedUnitFontColor = new ColorProperty("speedUnitFontColor", "speedUnitFontColor");
    private final ColorProperty speedFontColor = new ColorProperty("speedFontColor", "speedFontColor");
    private final ColorProperty rpmFontColor = new ColorProperty("rpmFontColor", "rpmFontColor");
    private final ImageProperty ImgBrake = new ImageProperty("ImgBrake", null, "prunn/V8/brake.png", false, false);
    private final ImageProperty ImgRPM = new ImageProperty("ImgRPM", null, "prunn/V8/rpm.png", false, false);
    private final ImageProperty ImgSpeed = new ImageProperty("ImgSpeed", null, "prunn/V8/speed.png", false, false);
    private final ImagePropertyWithTexture imgGear = new ImagePropertyWithTexture( "imgGear", "prunn/V8/gear.png" );
    private final ImagePropertyWithTexture ImgRevmetter = new ImagePropertyWithTexture( "image", null, "prunn/V8/rev.png", false, false ); 
    private IntValue cSpeed = new IntValue();
    private IntValue cRPM = new IntValue();
    private IntValue cGear = new IntValue();
    private TransformableTexture texBrake1;
    private TransformableTexture texBrake2;
    private TransformableTexture texRPM1;
    private TransformableTexture texRPM2;
    private TransformableTexture texSpeed1;
    private TransformableTexture texSpeed2;
    private boolean BrakeDirty = false;
    private boolean RPMDirty = false;
    private boolean SpeedDirty = false;
    protected final BooleanProperty useMaxRevLimit = new BooleanProperty("useMaxRevLimit", "useMaxRevLimit", false);
    private IntValue TrackTemp = new IntValue();
    
    
    public String getDefaultNamedColorValue(String name)
    {
        if(name.equals("StandardBackground"))
            return "#00000000";
        if(name.equals("StandardFontColor"))
            return "#FFFFFF";
        if(name.equals("speedFontColor"))
            return "#FFFFFF";
        if(name.equals("rpmFontColor"))
            return "#C9C9C9";
        if(name.equals("speedUnitFontColor"))
            return "#D7883B";

        return null;
    }
    
    @Override
    public String getDefaultNamedFontValue(String name)
    {
        if(name.equals("StandardFont"))
            return FontUtils.getFontString("Dialog", 1, 16, true, true);
        if(name.equals("speedF11Font"))
            return FontUtils.getFontString("Dialog", 1, 32, true, true);
        if(name.equals("speedUnitFont"))
            return FontUtils.getFontString("Dialog", 1, 22, true, true);
        if(name.equals("gearF11Font"))
            return FontUtils.getFontString("Dialog", 1, 24, true, true);
        if(name.equals("TBFont"))
            return FontUtils.getFontString("Dialog", 1, 20, true, true);
        
        return null;
    }
    
    
    protected Boolean onVehicleControlChanged(VehicleScoringInfo viewedVSI, LiveGameData gameData, boolean isEditorMode)
    {
        super.onVehicleControlChanged(viewedVSI, gameData, isEditorMode);
        
        return viewedVSI.isPlayer();
    }
    
    
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
        
        int wTB = widgetInnerWidth*13/100;
        int hTB = widgetInnerHeight*12/100;
        
        //"BRAKE"
        if(texBrake1 == null || texBrake1.getWidth() != wTB || texBrake1.getHeight() != hTB || BrakeDirty)
        {
            texBrake1 = TransformableTexture.getOrCreate(wTB, hTB, true, texBrake1, isEditorMode);
            texBrake2 = TransformableTexture.getOrCreate(wTB, hTB, true, texBrake2, isEditorMode);
            ImageTemplate it = ImgBrake.getImage();
            it.drawScaled(0, 0, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, wTB, hTB, texBrake1.getTexture(), true);
            it.drawScaled(0, it.getBaseHeight() / 2, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, wTB, hTB, texBrake2.getTexture(), true);
            texBrake1.setTranslation(widgetInnerWidth*86/100, widgetInnerHeight*83/100);
            texBrake2.setTranslation(widgetInnerWidth*86/100, widgetInnerHeight*83/100);
            texBrake1.setLocalZIndex(501);
            texBrake2.setLocalZIndex(502);
            BrakeDirty = false;
        }
        collector.add(texBrake1);
        collector.add(texBrake2);
        
      //RPM
        int wRPM = widgetInnerWidth*38/100;
        int hRPM = widgetInnerHeight*12/100;
        
        if(texRPM1 == null || texRPM1.getWidth() != wRPM || texRPM1.getHeight() != hRPM || RPMDirty)
        {
            texRPM1 = TransformableTexture.getOrCreate(wRPM, hRPM, true, texRPM1, isEditorMode);
            texRPM2 = TransformableTexture.getOrCreate(wRPM, hRPM, true, texRPM2, isEditorMode);
            ImageTemplate it = ImgRPM.getImage();
            it.drawScaled(0, 0, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, wRPM, hRPM, texRPM1.getTexture(), true);
            it.drawScaled(0, it.getBaseHeight() / 2, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, wRPM, hRPM, texRPM2.getTexture(), true);
            texRPM1.setTranslation(widgetInnerWidth*53/100, widgetInnerHeight*49/100);
            texRPM2.setTranslation(widgetInnerWidth*53/100, widgetInnerHeight*49/100);
            texRPM1.setLocalZIndex(501);
            texRPM2.setLocalZIndex(502);
            RPMDirty = false;
        }
        collector.add(texRPM1);
        collector.add(texRPM2);
        
      //Speed
        int wSpeed = widgetInnerWidth*43/100;
        int hSpeed = widgetInnerHeight*23/100;
        
        if(texSpeed1 == null || texSpeed1.getWidth() != wSpeed || texSpeed1.getHeight() != hSpeed || SpeedDirty)
        {
            texSpeed1 = TransformableTexture.getOrCreate(wSpeed, hSpeed, true, texSpeed1, isEditorMode);
            texSpeed2 = TransformableTexture.getOrCreate(wSpeed, hSpeed, true, texSpeed2, isEditorMode);
            ImageTemplate it = ImgSpeed.getImage();
            it.drawScaled(0, 0, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, wSpeed, hSpeed, texSpeed1.getTexture(), true);
            it.drawScaled(0, it.getBaseHeight() / 2, it.getBaseWidth(), it.getBaseHeight() / 2, 0, 0, wSpeed, hSpeed, texSpeed2.getTexture(), true);
            texSpeed1.setTranslation(widgetInnerWidth*56/100, widgetInnerHeight*13/100);
            texSpeed2.setTranslation(widgetInnerWidth*56/100, widgetInnerHeight*13/100);
            texSpeed1.setLocalZIndex(501);
            texSpeed2.setLocalZIndex(502);
            SpeedDirty = false;
        }
        collector.add(texSpeed1);
        collector.add(texSpeed2);
    }
    
    @Override
    protected void initialize( LiveGameData gameData, boolean isEditorMode, DrawnStringFactory drawnStringFactory, TextureImage2D texture, int width, int height )
    {
        int fhSpeed = TextureImage2D.getStringHeight( "09gy", speedF11Font );
        int fhV8 = TextureImage2D.getStringHeight( "09gy", V8Font);
        int fhTB = TextureImage2D.getStringHeight( "09gy", TBFont);
        int fhSpeedUnit = TextureImage2D.getStringHeight( "09gy", speedUnitFont );
        
        dsName = drawnStringFactory.newDrawnString( "dsName", width*2/100, height*54/100 - fhV8/2, Alignment.LEFT, false, V8Font.getFont(), isFontAntiAliased(), fontColor3.getColor() );
        dsSpeed = drawnStringFactory.newDrawnString( "dsSpeed", width*11/100, height*80/100 - fhSpeed/2, Alignment.RIGHT, false, speedF11Font.getFont(), isFontAntiAliased(), speedFontColor.getColor() );
        dsSpeedUnit = drawnStringFactory.newDrawnString( "dsSpeedUnit", width*12/100, height*80/100 - fhSpeedUnit/2, Alignment.LEFT, false, speedUnitFont.getFont(), isFontAntiAliased(), speedUnitFontColor.getColor() );
        dsRPM = drawnStringFactory.newDrawnString( "dsSpeed", width*35/100, height*80/100 - fhSpeed/2, Alignment.RIGHT, false, speedF11Font.getFont(), isFontAntiAliased(), rpmFontColor.getColor() );
        dsRPMUnit = drawnStringFactory.newDrawnString( "dsSpeedUnit", width*36/100, height*80/100 - fhSpeedUnit/2, Alignment.LEFT, false, speedUnitFont.getFont(), isFontAntiAliased(), speedUnitFontColor.getColor() );
        dsTrack = drawnStringFactory.newDrawnString( "dsTrack", width*58/100, height*90/100 - fhTB/2, Alignment.RIGHT, false, TBFont.getFont(), isFontAntiAliased(), speedFontColor.getColor());
        dsTrackTemp = drawnStringFactory.newDrawnString( "dsTrackTemp", width*62/100, height*90/100 - fhSpeedUnit/2, Alignment.LEFT, false, speedUnitFont.getFont(), isFontAntiAliased(), speedUnitFontColor.getColor() );
        dsBrake = drawnStringFactory.newDrawnString( "dsBrake", width*82/100, height*90/100 - fhTB/2, Alignment.RIGHT, false, TBFont.getFont(), isFontAntiAliased(), speedFontColor.getColor());
        
        dsGear1 = drawnStringFactory.newDrawnString( "dsGear1", width*116/200, height*70/100 - fhTB/2, Alignment.RIGHT, false, gearF11Font.getFont(), isFontAntiAliased(), fontColor3.getColor());
        dsGear2 = drawnStringFactory.newDrawnString( "dsGear1", width*131/200, height*70/100 - fhTB/2, Alignment.RIGHT, false, gearF11Font.getFont(), isFontAntiAliased(), fontColor3.getColor());
        dsGear3 = drawnStringFactory.newDrawnString( "dsGear1", width*73/100, height*70/100 - fhTB/2, Alignment.RIGHT, false, gearF11Font.getFont(), isFontAntiAliased(), fontColor3.getColor());
        dsGear4 = drawnStringFactory.newDrawnString( "dsGear1", width*81/100, height*70/100 - fhTB/2, Alignment.RIGHT, false, gearF11Font.getFont(), isFontAntiAliased(), fontColor3.getColor());
        dsGear5 = drawnStringFactory.newDrawnString( "dsGear1", width*89/100, height*70/100 - fhTB/2, Alignment.RIGHT, false, gearF11Font.getFont(), isFontAntiAliased(), fontColor3.getColor());
        dsGear6 = drawnStringFactory.newDrawnString( "dsGear1", width*97/100, height*70/100 - fhTB/2, Alignment.RIGHT, false, gearF11Font.getFont(), isFontAntiAliased(), fontColor3.getColor());
        
        ImgRevmetter.updateSize( width, height, isEditorMode );
        texGear = imgGear.getImage().getScaledTextureImage( width*8/100, height*17/100, texGear, isEditorMode );
        
        
    }
    protected Boolean updateVisibility(LiveGameData gameData, boolean isEditorMode)
    {
        
        super.updateVisibility(gameData, isEditorMode);
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        TelemetryData telemData = gameData.getTelemetryData();
        cGear.update( telemData.getCurrentGear() );
        
        if(cGear.hasChanged() && !isEditorMode)
            forceCompleteRedraw(true);
        if(scoringInfo.getViewedVehicleScoringInfo().isPlayer())
            return true; 
        
        return false;
         
    }
    
    @Override
    protected void drawBackground( LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height, boolean isRoot )
    {
        super.drawBackground( gameData, isEditorMode, texture, offsetX, offsetY, width, height, isRoot );
        texture.drawImage( ImgRevmetter.getTexture(), offsetX, offsetY, true, null );
        
        if(cGear.getValue() == 1)
            texture.drawImage( texGear, offsetX + width*53/100, offsetY + height*63/100, true, null );
        else if(cGear.getValue() == 2)
            texture.drawImage( texGear, offsetX + width*61/100, offsetY + height*63/100, true, null );
        else if(cGear.getValue() == 3)
            texture.drawImage( texGear, offsetX + width*68/100, offsetY + height*63/100, true, null );
        else if(cGear.getValue() == 4)
            texture.drawImage( texGear, offsetX + width*76/100, offsetY + height*63/100, true, null );
        else if(cGear.getValue() == 5)
            texture.drawImage( texGear, offsetX + width*84/100, offsetY + height*63/100, true, null );
        else if(cGear.getValue() == 6)
            texture.drawImage( texGear, offsetX + width*92/100, offsetY + height*63/100, true, null );
           
    }
    
    @Override
    protected void drawWidget( Clock clock, boolean needsCompleteRedraw, LiveGameData gameData, boolean isEditorMode, TextureImage2D texture, int offsetX, int offsetY, int width, int height )
    {
        ScoringInfo scoringInfo = gameData.getScoringInfo();
        TelemetryData telemData = gameData.getTelemetryData();
        float uBrake = isEditorMode ? 0.4F : telemData.getUnfilteredBrake();
        int wT = texBrake2.getWidth();
        int wRPM = texRPM2.getWidth();
        int brake = (int)((float)wT * uBrake);
        float uRPM;
        if ( useMaxRevLimit.getBooleanValue() )
            uRPM = isEditorMode ? 0.9F : telemData.getEngineRPM() / gameData.getPhysics().getEngine().getRevLimitRange().getMaxValue();
        else
            uRPM = isEditorMode ? 0.9F : telemData.getEngineRPM() / 8500;
        
        int rpm = (int)((float)wRPM * uRPM);
        
        texRPM2.setClipRect(0, 0, rpm, texRPM2.getHeight(), true);
        
        texBrake2.setClipRect(0, 0, brake, texBrake2.getHeight(), true);
        
        cRPM.update( (int)telemData.getEngineRPM() );
        TrackTemp.update((int)Math.floor(gameData.getWeatherInfo().getTrackTemperature()));
        cSpeed.update( (int)telemData.getScalarVelocityKmh() );
        
        if(cGear.getValue() == 1)
            dsGear1.draw( offsetX, offsetY, "1", speedFontColor.getColor(), texture );
        else
            dsGear1.draw( offsetX, offsetY, "1", texture );
        
        if(cGear.getValue() == 2)
            dsGear2.draw( offsetX, offsetY, "2", speedFontColor.getColor(), texture );
        else
            dsGear2.draw( offsetX, offsetY, "2", texture );
        
        if(cGear.getValue() == 3)
            dsGear3.draw( offsetX, offsetY, "3", speedFontColor.getColor(), texture );
        else
            dsGear3.draw( offsetX, offsetY, "3", texture );
        if(cGear.getValue() == 4)
            dsGear4.draw( offsetX, offsetY, "4", speedFontColor.getColor(), texture );
        else
            dsGear4.draw( offsetX, offsetY, "4", texture );
        if(cGear.getValue() == 5)
            dsGear5.draw( offsetX, offsetY, "5", speedFontColor.getColor(), texture );
        else
            dsGear5.draw( offsetX, offsetY, "5", texture );
        if(cGear.getValue() == 6)
            dsGear6.draw( offsetX, offsetY, "6", speedFontColor.getColor(), texture );
        else
            dsGear6.draw( offsetX, offsetY, "6", texture );
        
        
         
        float uSpeed = isEditorMode ? 0.9F : telemData.getScalarVelocityKmh()/340;
        int wSpeed = texSpeed2.getWidth();
        int speed = (int)((float)wSpeed * uSpeed);
        texSpeed2.setClipRect(0, 0, speed, texSpeed2.getHeight(), true);
        
        
        if(needsCompleteRedraw)
        {
            dsTrack.draw( offsetX, offsetY, "TRACK", texture );
            dsBrake.draw( offsetX, offsetY, "BRAKE", texture );
            dsName.draw( offsetX, offsetY, scoringInfo.getViewedVehicleScoringInfo().getDriverName(), texture );
        }
        if(needsCompleteRedraw || TrackTemp.hasChanged())
          dsTrackTemp.draw( offsetX, offsetY, TrackTemp.getValueAsString() + "°", texture );
            
        if(needsCompleteRedraw || cSpeed.hasChanged())
        {
            dsSpeed.draw( offsetX, offsetY, String.valueOf( cSpeed.getValue() ), texture );
            dsSpeedUnit.draw( offsetX, offsetY, "km/h", texture );
        }
        if(needsCompleteRedraw || (cRPM.hasChanged() && clock.c()))
        {
            dsRPM.draw( offsetX, offsetY, String.valueOf( cRPM.getValue() ), texture );
            dsRPMUnit.draw( offsetX, offsetY, "rpm", texture );
        }
    }
    
    
    @Override
    public void saveProperties( PropertyWriter writer ) throws IOException
    {
        super.saveProperties( writer );
        writer.writeProperty( speedF11Font, "" );
        writer.writeProperty( speedFontColor, "" );
        writer.writeProperty( speedUnitFont, "" );
        writer.writeProperty( speedUnitFontColor, "" );
        writer.writeProperty( gearF11Font, "" );
        writer.writeProperty( fontColor3, "" );
        writer.writeProperty( TBFont, "" );
        writer.writeProperty( rpmFontColor, "" );
        writer.writeProperty( useMaxRevLimit, "" );
        writer.writeProperty( V8Font, "" );
        
    }
    
    @Override
    public void loadProperty( PropertyLoader loader )
    {
        super.loadProperty( loader );
        if ( loader.loadProperty( speedF11Font ) );
        else if ( loader.loadProperty( speedFontColor ) );
        else if ( loader.loadProperty( speedUnitFont ) );
        else if ( loader.loadProperty( speedUnitFontColor ) );
        else if ( loader.loadProperty( gearF11Font ) );
        else if ( loader.loadProperty( fontColor3 ) );
        else if ( loader.loadProperty( TBFont ) );
        else if ( loader.loadProperty( rpmFontColor ) );
        else if ( loader.loadProperty( useMaxRevLimit ) );
        else if ( loader.loadProperty( V8Font ) );
        
        
    }
    
    @Override
    public void getProperties( PropertiesContainer propsCont, boolean forceAll )
    {
        super.getProperties( propsCont, forceAll );
        
        propsCont.addGroup( "Misc" );
        propsCont.addProperty( speedF11Font );
        propsCont.addProperty( speedFontColor );
        propsCont.addProperty( speedUnitFont );
        propsCont.addProperty( speedUnitFontColor );
        propsCont.addProperty( gearF11Font );
        propsCont.addProperty( fontColor3 );
        propsCont.addProperty( V8Font );
        propsCont.addProperty( TBFont );
        propsCont.addProperty( rpmFontColor );
        propsCont.addProperty( useMaxRevLimit );
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
    
    public RevMetterWidget()
    {
        super( PrunnWidgetSetv8.INSTANCE, PrunnWidgetSetv8.WIDGET_PACKAGE_V8, 48.1f, 16.0f );
        getBackgroundProperty().setColorValue( "#00000000" );
        
    }
    
}
