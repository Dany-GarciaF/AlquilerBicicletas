// Fixed port (backend CORS and Google callback expect it) and SPA fallback so /auth/google/callback serves index.html.
config.devServer = config.devServer || {};
config.devServer.port = 3000;
config.devServer.historyApiFallback = true;
