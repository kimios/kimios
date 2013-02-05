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
Ext.onReady(function() {
  var defaultLang = kimios.getLanguage();
  Ext.QuickTips.init();
  kimios.store.getLangStore(defaultLang).load( {
    callback : function(records, options, success) {
      new kimios.explorer.Viewport( {
        checkSession : 120, // in seconds
        i18n : new kimios.i18n.Internationalization( {
          lang : defaultLang,
          records : records
        })
      });
    }
  });

  //TODO History Management
//  Ext.History.init(function(){
//    Ext.History.on('change', function(token){
//       if (token){
//         var uid;
//         var type;
//         
//         if (token == 'start'){
//           uid = undefined;
//           type = undefined;
//         }else{
//           var p = token.split(':');
//           uid = p[0];
//           type = p[1];
//         }
//
//         kimios.explorer.getActivePanel().loadEntity({
//           uid : uid,
//           type : type
//         }); 
//
//          }else{
//            // prevent back
//            Ext.History.forward();
//          }
//      });
//  }, this);
});

