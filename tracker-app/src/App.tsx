
import './App.css'
import { FormContainer } from './components/forms'
import { AnalyticsContainer } from './components/analytics'
import { TableContainer } from './components/table'
import { useState } from 'react';

type Container = "form" | "analytics" | "table";
function App() {
  const [container, setContainer] = useState<Container>("form");

  return (
    <main className='flex flex-col justify-center gap-8 py-2'>
      <h1 className='text-center text-5xl font-bold'>Welcome Back Argel</h1>
      <div className=''>
        {
          container === "form" ? <FormContainer />
            : container === "analytics" ? <AnalyticsContainer />
            : <TableContainer />
        }
      </div>
      <span className="flex justify-center gap-4 py-2">
        <button
          className="btn btn-primary"
          onClick={() => setContainer("form")}
        >
          Insert New Data
        </button>
        <button
          className="btn btn-primary"
          onClick={() => setContainer("analytics")}
        >
          See Analytics
        </button>
        <button
          className="btn btn-primary"
          onClick={() => setContainer("table")}
        >
          See Tables
        </button>
      </span>
    </main>
  )
}

export default App
