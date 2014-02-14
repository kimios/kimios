/*
 * Kimios - Document Management System Software
 * Copyright (C) 2008-2014  DevLib'
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
 * aong with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

!function( $ ) {

 "use strict"

  var Combobox = function ( element, options ) {
    this.options = $.extend({}, $.fn.combobox.defaults, options)
    this.$container = this.setup(element)
    this.$element = this.$container.find('input')
    this.$button = this.$container.find('.dropdown-toggle')
    this.$target = this.$container.find('select')
    this.matcher = this.options.matcher || this.matcher
    this.sorter = this.options.sorter || this.sorter
    this.highlighter = this.options.highlighter || this.highlighter
    this.$menu = $(this.options.menu).appendTo('body')
    this.placeholder = this.options.placeholder || this.$target.attr('data-placeholder')
    this.$element.attr('placeholder', this.placeholder)
    this.shown = false
    this.selected = false
    this.refresh()
    this.listen()
  }

  /* NOTE: COMBOBOX EXTENDS BOOTSTRAP-TYPEAHEAD.js
     ========================================== */

  Combobox.prototype = $.extend({}, $.fn.typeahead.Constructor.prototype, {

    constructor: Combobox

  , setup: function (element) {
      var select = $(element)
        , combobox = $(this.options.template)
      select.before(combobox)
      select.detach()
      combobox.append(select)
      return combobox
    }

  , parse: function () {
      var map = {}
        , source = []
        , selected = false
      this.$target.find('option').each(function() {
        var option = $(this)
        map[option.text()] = option.val()
        source.push(option.text())
        if(option.attr('selected')) selected = option.html()
      })
      this.map = map
      if (selected) {
        this.$element.val(selected)
        this.$container.addClass('combobox-selected')
        this.selected = true
      }
      return source
    }

  , toggle: function () {
    if (this.$container.hasClass('combobox-selected')) {
      this.clearTarget()
      this.$element.val('').focus()
    } else {
      if (this.shown) {
        this.hide()
      } else {
        this.lookup()
      }
    }
  }

  , clearTarget: function () {
    this.$target.val('')
    this.$container.removeClass('combobox-selected')
    this.selected = false
    this.$target.trigger('change')
  }

  , refresh: function () {
    this.source = this.parse()
    this.options.items = this.source.length
  }

  // modified typeahead function adding container and target handling
  , select: function () {
      var val = this.$menu.find('.active').attr('data-value')
      this.$element.val(val)
      this.$container.addClass('combobox-selected')
      this.$target.val(this.map[val])
      this.$target.trigger('change')
      this.selected = true
      return this.hide()
    }

  // modified typeahead function removing the blank handling
  , lookup: function (event) {
      var that = this
        , items
        , q

      this.query = this.$element.val()

      items = $.grep(this.source, function (item) {
        if (that.matcher(item)) return item
      })

      items = this.sorter(items)

      if (!items.length) {
        return this.shown ? this.hide() : this
      }

      return this.render(items.slice(0, this.options.items)).show()
    }

  // modified typeahead function adding button handling
  , listen: function () {
      this.$element
        .on('blur',     $.proxy(this.blur, this))
        .on('keypress', $.proxy(this.keypress, this))
        .on('keyup',    $.proxy(this.keyup, this))

      if ($.browser.webkit || $.browser.msie) {
        this.$element.on('keydown', $.proxy(this.keypress, this))
      }

      this.$menu
        .on('click', $.proxy(this.click, this))
        .on('mouseenter', 'li', $.proxy(this.mouseenter, this))

      this.$button
        .on('click', $.proxy(this.toggle, this))
    }

  // modified typeahead function to clear on type and prevent on moving around
  , keyup: function (e) {
      switch(e.keyCode) {
        case 40: // down arrow
        case 39: // right arrow
        case 38: // up arrow
        case 37: // left arrow
        case 36: // home
        case 35: // end
        case 16: // shift
          break

        case 9: // tab
        case 13: // enter
          if (!this.shown) return
          this.select()
          break

        case 27: // escape
          if (!this.shown) return
          this.hide()
          break

        default:
          this.clearTarget()
          this.lookup()
      }

      e.stopPropagation()
      e.preventDefault()
  }

  // modified typeahead function to only hide menu if it is visible
  , blur: function (e) {
      var that = this
      e.stopPropagation()
      e.preventDefault()
      var val = this.$element.val()
      if (!this.selected && val != "" ) {
        this.$element.val("")
        this.$target.val("").trigger('change')
      }
      if (this.shown) {
        setTimeout(function () { that.hide() }, 150)
      }
    }
  })

  /* COMBOBOX PLUGIN DEFINITION
   * =========================== */

  $.fn.combobox = function ( option ) {
    return this.each(function () {
      var $this = $(this)
        , data = $this.data('combobox')
        , options = typeof option == 'object' && option
      if(!data) $this.data('combobox', (data = new Combobox(this, options)))
      if (typeof option == 'string') data[option]()
    })
  }

  $.fn.combobox.defaults = {
  template: '<div class="combobox-container"><input type="text" autocomplete="off" /><span class="add-on btn dropdown-toggle" data-dropdown="dropdown"><span class="caret"/><span class="combobox-clear"><i class="icon-remove"/></span></span></div>'
  , menu: '<ul class="typeahead typeahead-long dropdown-menu"></ul>'
  , item: '<li><a href="#"></a></li>'
  , placeholder: null
  }

  $.fn.combobox.Constructor = Combobox

}( window.jQuery );