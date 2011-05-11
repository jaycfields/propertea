task "publish:fig", :v do |_, args|
  sh "lein jar"
  sh "fig --publish propertea/#{args[:v]}"
end

task "publish:clojars" do |_, args|
  sh "lein jar"
  sh "lein pom"
  sh "scp pom.xml propertea.jar clojars@clojars.org:"
end
