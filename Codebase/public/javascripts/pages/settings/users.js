$(function() {
	
	var Models 		= window.Models;
	var Collections = window.Collections;
	var Views 		= window.Views;
	
	Views.Profiles = Views.Table.extend({
		
		headers : [ "Username", "Email", "First name", "Last name", "Role", "Active", "Actions" ],
				
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
		
		className : 'user table__row',
		
		model : Models.User,
		
		template : _.template($("#profile").html()),
		
		events: {
			'click .user__edit-btn' 		: 'edit',
			'click .user__delete-btn' 		: 'delete'
		},
		
		initialize : function () {
			_.bindAll(this, 'render');
			this.model.bind('change', this.render);
			this.render();
		},
		
		edit : function () {
			var popup = new Views.ProfileSettings({ model : this.model, title : "Edit user"});
			popup.show();
			return this;
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
		
		className : 'button button_type_add btn btn-primary',
		
		tagName : 'button',
		
		events: {
			'click' : 'openSettings',
		},
		
		initialize : function () {
			_.bindAll(this, 'render');
			this.render();
		},
		
		openSettings : function () {
			var popup = new Views.ProfileSettings({ model : new Models.User(), title : "Add user" });
			popup.show();
			return this;
		},
		
		render : function () {
			this.$el.html("Add user");
		}
		
	});
	
	Views.UserProfileForm = Backbone.View.extend({
		
		tagName : 'form',
		
		className : 'profile-settings__form',
		
		template : _.template($("#profile-form").html()),
		
		initialize : function (options) {
			
			_.bindAll(this, 'render');
			
			this.options = options;
			this.render();
		},
		
		render : function () {
			this.$el.html(this.template(_.extend(this.model.toJSON(), { roles : ["ADMIN", "USER"] })));
		}
	});
	
	Views.ProfileSettings = Views.Modal.extend({
		
		initialize : function (options) {
			Views.Modal.prototype.initialize.call(this, options);
			_.bindAll(this, 'render', 'successSaving', 'errorSaving');
			Backbone.Validation.bind(this);
		},
		
		format : function (sourceData) {
	    	var formattedData = sourceData;

	    	formattedData.isActive === 'on' ? formattedData.isActive = true : formattedData.isActive = false;
	    	
	    	return formattedData;
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
			Collections.Users.add(this.model);
			Views.Modal.prototype.save.call(this);
		},
		
		errorSaving : function (model, response) {
			
			this.$el.find('.button_type_save').removeAttr('disabled');
			
			var data = JSON.parse(response.responseText);
			this.highlightErrors(data.errors);
		},
		
		save : function () {
			
			var data = this.format(this.$el.find('.profile-settings__form').serializeObject());
			
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
			
			this.$el.find('.modal-body').append(new Views.UserProfileForm({ model : this.model }).el);
			
			return this;
		}
	});
	
	var users = new Views.Profiles({collection: Collections.Users});
	
	var addUser = new Views.AddProfile();
	
	$('.content__main').append(addUser.el);
	$('.content__main').append(users.el);
	
});
