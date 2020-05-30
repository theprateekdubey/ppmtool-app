import React from "react";
import "./App.css";
import Dashboard from "./components/Dashboard";
import AddProject from "./components/projects/AddProject";
import Header from "./components/layout/Header";
import "bootstrap/dist/css/bootstrap.min.css";
import { BrowserRouter as Router, Route } from "react-router-dom";
import { Provider } from "react-redux";
import store from "./store";
import UpdateProject from "./components/projects/UpdateProject";
import ProjectBoard from "./components/ProjectBoard/ProjectBoard";
import AddProjectTask from "./components/ProjectBoard/ProjectTasks/AddProjectTasks";
function App() {
  return (
    <Provider store={store}>
      <Router>
        <div>
          <Header />
        </div>
        <Route exact path="/dashboard" component={Dashboard} />
        <Route exact path="/addProject" component={AddProject} />
        <Route exact path="/updateProject/:id" component={UpdateProject} />
        <Route exact path="/projectBoard/:id" component={ProjectBoard} />
        <Route exact path="/addProjectTask/:id" component={AddProjectTask} />
      </Router>
    </Provider>
  );
}

export default App;
