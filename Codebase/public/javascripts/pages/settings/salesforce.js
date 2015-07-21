$(function() {
	
	var Models 		= window.Models;
	var Collections = window.Collections;
	var Views 		= window.Views;

	Views.Profiles = Views.Table.extend({
		
		headers : [ "Name", "Actions" ],
				
		render : function () {
			
			Views.Table.prototype.render.call(this);
			
			this.collection.each(function (profile) {
				this.$el
					.find('tbody')
					.append(new Views.Profile({ model: profile }).el);
			}, this);
			
			return this;
		}
		
	});
	
	Views.Profile = Backbone.View.extend({
		
		tagName : 'tr',
		
		className : 'profile table__row',
		
		model : Models.Profile,
		
		apiSettings : Models.ApiSettings,
		
		template : _.template($("#profile").html()),
		
		events: {
			'click .profile__edit-btn' 		: 'edit',
			'click .profile__delete-btn' 	: 'delete'
		},
		
		initialize : function () {
			_.bindAll(this, 'render');
			this.model.bind('change', this.render);
			this.render();
		},
		
		edit : function () {
			var popup = new Views.ProfileSettings({ model : this.model, title : 'Edit profile' });
			popup.show();
		},
		
		delete : function () {
			this.model.collection.remove(this.model);
			this.model.destroy();
			this.$el.remove();
			return this;
		},
		
		render : function () {
			this.$el.html(this.template(this.model.toJSON()));
			return this;
		}
	});
	
	Views.AddProfile = Backbone.View.extend({
		
		tagName : 'button',
		
		className : 'button button_type_add btn btn-primary',
		
		events: {
			'click' : 'openSettings',
		},
		
		initialize : function () {
			_.bindAll(this, 'render');
			this.render();
		},
		
		openSettings : function () {
			var popup = new Views.ProfileSettings({ model : new Models.Profile(), title : "Add profile" });
			popup.show();
			return this;
		},
		
		render : function () {
			this.$el.html("Add profile");
		}
	});
	
	Views.SalesforceProfileForm = Backbone.View.extend({
		
		tagName : 'form',
		
		className : 'profile-settings__form',
		
		template : _.template($("#profile-form").html()),
		
		initialize : function (options) {
			
			_.bindAll(this, 'render');
			
			this.options = options;
			this.render();
		},
		
		render : function () {
			this.$el.html(this.template(this.model.toJSON()))
		}
	});
	
	Views.ProfileSettings = Views.Modal.extend({
		
		initialize : function (options) {
			Views.Modal.prototype.initialize.call(this, options);
			_.bindAll(this, 'render', 'successSaving', 'errorSaving');
			Backbone.Validation.bind(this);
		},
		
	    highlightErrors : function (errors) {
	    	
	    	this.$el.find('.profile-settings__form .form-group').removeClass('has-warning');
	    	
	    	_.each(errors, function (message, field) {
    			this.$el.find('.profile-settings__form ' + '[name="' + field  + '"]' ).closest('.form-group').addClass('has-warning');
    		}, this);
	    	
	    	this.$el.find('.profile-settings__form .form-group[class*="has-"]:first-child .form-control').focus();
	    },
	    
	    successSaving : function (model, response) {
	    	
	    	this.$el.find('.button_type_save').removeAttr('disabled');
	    	
	    	this.model.trigger('change');
			Collections.Profiles.add(this.model);
			Views.Modal.prototype.save.call(this);
		},
		
		errorSaving : function (model, response) {
			
			this.$el.find('.button_type_save').removeAttr('disabled');
			
			var data = JSON.parse(response.responseText);
			this.highlightErrors(data.errors);
		},
		
		save : function () {
			
			var data = this.$el.find('.profile-settings__form').serializeObject();
			
			this.model.set(data, { silent : true });
	    	
	    	if (this.model.isValid(true)) {
	    		if (this.model.hasChanged() || this.model.isNew()) {
	    			this.model.save(null, { success : this.successSaving, error : this.errorSaving });
	    			this.$el.find('.button_type_save').attr('disabled', true);
	    		}
	    	} else {
	    		this.highlightErrors(this.model.validate());	
	    	}

		},
		
		teardown : function() {
			Views.Modal.prototype.teardown.call(this);
	    	this.remove();
	    },
		
		render : function () {
			
			Views.Modal.prototype.render.call(this);
			
			this.$el.find('.modal-body').append(new Views.SalesforceProfileForm({ model : this.model }).el);
			
			return this;
		}
	});
	
	var profiles = new Views.Profiles({collection: Collections.Profiles});
	
	var addProfile = new Views.AddProfile();
	
	$('.content__main').append(addProfile.el);
	$('.content__main').append(profiles.el);
	
});
