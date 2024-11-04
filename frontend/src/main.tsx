import {StrictMode, Suspense} from 'react'
import {createRoot} from 'react-dom/client'
import App from './component/App.tsx'
import "./i18n/config.ts";
import {BrowserRouter} from "react-router-dom";

createRoot(document.getElementById('root')!).render(
    <StrictMode>
        <Suspense fallback={<div>Loading...</div>}>
            <BrowserRouter>
                <App/>
            </BrowserRouter>
        </Suspense>
    </StrictMode>
)
