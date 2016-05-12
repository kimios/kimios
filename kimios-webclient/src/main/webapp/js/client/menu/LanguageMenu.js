/*
 * Kimios - Document Management System Software
 * Copyright (C) 2012-2013  DevLib'
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 2 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
kimios.menu.LanguageMenu = Ext.extend(Ext.Button,{
  constructor : function(config) {
    this.id = 'kimios-language';
    this.iconCls = 'lang-'+kimios.explorer.getViewport().i18n.getLang();
    this.tooltip = kimios.lang('DefaultLanguage');
    var defaultLang = kimios.getBrowserLanguage();
    
    this.defItem = new Ext.menu.Item({
      id : defaultLang+'-flag',
      text : null, // set later
      iconCls : 'lang-'+defaultLang,
      handler : function(button, event){
        kimios.explorer.getViewport().changeLanguage(lang);
      }
    });
    
    this.enItem = new Ext.menu.Item({
      id : 'en-flag',
      text : 'English',
      iconCls : 'lang-en',
      handler : function(button, event){
        kimios.explorer.getViewport().changeLanguage('en');
      }
    });
    
    this.frItem = new Ext.menu.Item({
      id : 'fr-flag',
      text : 'Fran&ccedil;ais',
      iconCls : 'lang-fr',
      handler : function(button, event){
        kimios.explorer.getViewport().changeLanguage('fr');
      }
    });
    
    this.esItem = new Ext.menu.Item({
      id : 'es-flag',
      text : 'Espa&ntilde;ol',
      iconCls : 'lang-es',
      handler : function(button, event){
        kimios.explorer.getViewport().changeLanguage('es');
      }
    });
    
    this.deItem = new Ext.menu.Item({
      id : 'de-flag',
      text : 'Deutsch',
      iconCls : 'lang-de',
      disabled : true,
      handler : function(button, event){
        kimios.explorer.getViewport().changeLanguage('de');
      }
    });
    
    this.plItem = new Ext.menu.Item({
      id : 'pl-flag',
      text : 'Polski',
      iconCls : 'lang-pl',
      handler : function(button, event){
        kimios.explorer.getViewport().changeLanguage('pl');
      }
    });

    this.itItem = new Ext.menu.Item({
        id : 'it-flag',
        text : 'Italiano',
        iconCls : 'lang-it',
        handler : function(button, event){
          kimios.explorer.getViewport().changeLanguage('it');
        }
      });
    
    this.menu = new Ext.menu.Menu({
      shadow : 'frame',
      showSeparator : false,
      enableScrolling : true,
      items : [this.defItem, '-', this.enItem, this.frItem, this.esItem, this.itItem, this.plItem]
    });
    
    kimios.menu.LanguageMenu.superclass.constructor.call(this, config);
  },
  
  initComponent : function(){
    kimios.menu.LanguageMenu.superclass.initComponent.apply(this, arguments);
    var lang = kimios.explorer.getViewport().i18n.getLang();
  },
  
  refreshLanguage : function(){
    var lang = kimios.explorer.getViewport().i18n.getLang();
    this.setTooltip(kimios.lang('DefaultLanguage'));
    this.setIconClass('lang-'+lang);
  }
});
