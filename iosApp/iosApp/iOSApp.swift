import SwiftUI
import shared

@main
struct iOSApp: App {
    init() {
        // Start Koin before any Compose content is created.
        IosBootstrapKt.setupKoin()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
                .ignoresSafeArea(.all)
                .preferredColorScheme(.dark)
        }
    }
}
