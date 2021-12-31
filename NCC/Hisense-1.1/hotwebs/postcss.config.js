module.exports = ({ file, options, env }) => {
  const pluginsConfig = {
    autoprefixer: {}
  };
  if(process.env.PROJECT_CLIENT === 'mobile') {
    pluginsConfig["postcss-px-to-viewport"] = {
            viewportWidth: 350,     // (Number) The width of the viewport.
            viewportHeight: 1334,    // (Number) The height of the viewport.
            unitPrecision: 3,       // (Number) The decimal numbers to allow the REM units to grow to.
            viewportUnit: 'vmin',     // (String) Expected units.
            fontViewportUnit: 'vmin',
            exclude: [],
            selectorBlackList: ['.ignore', '.hairlines'],  // (Array) The selectors to ignore and leave as px.
            minPixelValue: 1,       // (Number) Set the minimum pixel value to replace.
            mediaQuery: false       // (Boolean) Allow px to be converted in media queries.
        };
  }
  return {
    plugins: pluginsConfig
  };
};
