var gulp = require('gulp'),
	concat = require('gulp-concat'),
	watch = require('gulp-watch');
 
gulp.task('concat-less', function() {
  return gulp.src('./app/assets/stylesheets/blocks/**/*.less')
    .pipe(concat('index.less'))
    .pipe(gulp.dest('./app/assets/stylesheets/'));
});

gulp.task("watch", function () {
	watch("./app/assets/stylesheets/blocks/**/*.less", function () {
		gulp.start('concat-less');
	});
});