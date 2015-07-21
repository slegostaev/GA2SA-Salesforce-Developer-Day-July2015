$(function () {
	
	var Models 		= window.Models;
	var Collections = window.Collections;
	var Views 		= window.Views;
	
	Views.Profile = Backbone.View.extend({
		tagName : 'form',
		
		className : 'user-form',
		
		model : Models.Profile,
		
		template : _.template($("#profile").html()),
		
		events: {
			'click .button_type_save' : 'save'
		},
		
		initialize : function () {
			_.bindAll(this, 'render', 'successSaving', 'errorSaving');
			Backbone.Validation.bind(this);
			this.render();
		},
		
		highlightErrors : function (errors) {
	    	
	    	this.$el.find('.form-group').removeClass('has-warning');
	    	
	    	_.each(errors, function (message, field) {
    			this.$el.find('[name="' + field  + '"]' ).closest('.form-group').addClass('has-warning');
    		}, this);
	    	
	    	this.$el.find('.form-group[class*="has-"]:first-child .form-control').focus();
	    },
	    
	    successSaving : function (model, response) {
	    	
	    	this.$el.find('.button_type_save').removeAttr('disabled');
	    	
	    	this.model.trigger('change');
		},
		
		errorSaving : function (model, response) {
			
			this.$el.find('.button_type_save').removeAttr('disabled');
			
			var data = JSON.parse(response.responseText);
			this.highlightErrors(data.errors);
		},
		
		save : function (event) {
			
			event.preventDefault();
			
			var data = this.$el.serializeObject()
			
			this.model.set(data, { silent : true });
	    	
	    	if (this.model.isValid(true)) {
	    		if (this.model.hasChanged()) {
	    			this.model.save(null, { success : this.successSaving, error : this.errorSaving });
	    			this.$el.find('.button_type_save').attr('disabled', true);
	    		}
	    	} else {
	    		this.highlightErrors(this.model.validate());	
	    	}
			
			return this;
		},
		
		render : function () {
			this.$el.html(this.template(this.model.toJSON()));
			return this;
		}
	});
	
	var profile = new Views.Profile({ model: new Models.Profile() });
	
	$('.content__main').append(profile.el);
	
});