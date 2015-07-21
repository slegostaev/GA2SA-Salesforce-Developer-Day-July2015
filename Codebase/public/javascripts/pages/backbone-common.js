$(function () {
	if (!window.Models) 		window.Models = {};
	if (!window.Collections) 	window.Collections = {};
	if (!window.Views) 			window.Views = {};
	
	var Models 		= window.Models,
		Collections = window.Collections,
		Views		= window.Views;
	
	Views.Input = Backbone.View.extend({
		
		className : 'form-group',
		
		template : _.template($("#input").html()),
		
		initialize : function (options) {
			
			_.bindAll(this, 'render');
			
			this.options = options;
			this.$el.addClass(this.options.classes);
			this.render();
		},
		
		render : function () {
			
			this.$el.html(this.template({ 
				id 	 	: this.options._id,
				title 	: this.options.title,
				type	: this.options.type
			}));
			
			return this;
		}
	});
	
	Views.Select = Backbone.View.extend({
		
		className : 'form-group',
		
		template : _.template($("#select").html()),
		
		events : {
			'change select' : 'change'
		},
		
		data : null,
		
		initialize : function (options) {
			
			_.bindAll(this, 'render', 'change');
			
			this.options = options;
			this.collection.bind('all', this.render);
			this.$el.addClass(this.options.classes);
			this.render();
		},
		
		change : function () {
			return this;
		},
		
		render : function () {
			
			this.data = this.options.groupped ? _.groupBy(this.collection.toJSON(), this.options.groupField) : this.collection.toJSON();
			
			this.$el.html(this.template({ 
				id 		 : this.options._id,
				title 	 : this.options.title,
				multiple : this.options.multiple,
				groupped : this.options.groupped,
				items	 : this.data
			}));
			
			if (this.options.changeAfterInit) this.change();
			
			return this;
		}
		
	});
	
	Views.DependSelect = Views.Select.extend({
		
		initialize : function (options) {
			Views.Select.prototype.initialize.call(this, options);
			this.collection.bind('all', this.render);
		},
		
		change : function () {
			var dependSelects = this.options.dependSelects;
			_.each(dependSelects, function (select) {
				select.trigger(this.options._id + '.change', this.$el.find('select').val());
			}, this);
		},
		
		render : function () {
			
			Views.Select.prototype.render.call(this);
			return this;
		}
	});
	
	Views.Table = Backbone.View.extend({
		
		tagName : 'table',
		
		className : 'table',
		
		template : _.template($("#table").html()),
		
		initialize : function (options) {
			
			_.bindAll(this, 'render');
			
			this.options = options;
			this.collection.bind('add', this.render);
			this.$el.addClass(this.options.classes);
			this.render();
		},
		
		render : function () {
			this.$el.html(this.template({ headers : this.headers }));			
			return this;
		}
		
	});
	
	
	Views.Modal = Backbone.View.extend({
		
		className : 'modal fade',
		
		template : _.template($("#modal").html()),
		
		events : {
	      'hidden.bs.modal' : 'teardown',
	      'shown.bs.modal'	: 'showComplete',
	      'click .button_type_save' : 'save'
	    },

		initialize : function (options) {
			
			_.bindAll(this, 'render');
			
			this.options = options;
			this.$el.addClass(this.options.classes);
			this.render();
		},
		
		show : function() {
			this.$el.modal('show');
	    },
	    
	    showComplete : function () {
	    	this.$el.find('.form-group:first-child .form-control').focus();
	    },

	    teardown : function() {
	    	this.$el.data('modal', null);
	    },
	    
	    save : function () {
	    	this.$el.modal('hide');
	    },
		
		render : function () {
			this.$el.html(this.template({ 
				title : this.options.title
			}));
			this.$el.modal({show:false});
		}
	
	});
	
	Views.Alert = Backbone.View.extend({
		
		className : 'alert',
		
		template : _.template($("#alert").html()),

		initialize : function (options) {
			
			_.bindAll(this, 'render');
			
			this.options = options;
			this.$el.addClass('alert-' + this.options.typeAlert);
			this.render();
		},
		
		render : function () {
			this.$el.html(this.template({ 
				title : this.options.title,
				text : this.options.text
			}));
		}
	});
})


_.extend(Backbone.Validation.validators, {
	limit: function(value, attr, customValue, model) {
		if(value && value.length > customValue){
			return 'You have exceeded the number of elements in array';
		}
	}
});